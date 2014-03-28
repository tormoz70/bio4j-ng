package ru.bio4j.service.processing;

import ru.bio4j.model.transport.jstore.ColumnMetadata;
import ru.bio4j.model.transport.jstore.StoreData;
import ru.bio4j.model.transport.jstore.StoreMetadata;
import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.result.handlers.ResultHandler;
import ru.bio4j.service.sql.types.MetaTypeResolver;
import ru.bio4j.service.sql.types.TypeConverter;
import ru.bio4j.service.sql.types.TypeConverterBuilder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataResultHandler implements ResultHandler<StoreData> {

    private final StoreDataBuilder builder;

    public DataResultHandler(StoreDataBuilder builder) {
        this.builder = builder;
    }

    /**
     * @param rs
     * @param query
     * @param context
     * @return Результат, содержащий модель
     * @throws SQLException
     * @title Обработка результатов запроса объекта
     */
    @Override
    public StoreData handle(ResultSet rs, Query query, QueryContext context) throws SQLException {
        final StoreMetadataBuilder modelBuilder = new StoreMetadataBuilder();
        builder.metadata(modelBuilder);
        final ResultSetMetaData rsmd = rs.getMetaData();
        final TypeConverter converter =  TypeConverterBuilder.build(
                context.getDB().getTypeMapper(),
                context.get(MetaTypeResolver.class),
                rsmd,
                query.getResultTypes());
        //чтение метаинформации
        final StoreMetadata storeMetadata = buildModel(rsmd, modelBuilder, query, converter);
        List<ColumnMetadata> cols = storeMetadata.getFields();

        final int colCount = cols.size();

        final Map<String, Integer> fieldToIndex = new HashMap<>();
        for(int i = 0; i < colCount; ++i) {
            final ColumnMetadata columnMetadata = cols.get(i);
            fieldToIndex.put(columnMetadata.getName(), i);
        }


        try {
            while(rs.next()) {
                final List rows = new ArrayList(colCount);
                for(int i = 1; i < colCount + 1; ++i) {
                     rows.add(converter.read(rs, i, null, null));
                }
                builder.row(rows);
            }
            builder.fieldToIndex(fieldToIndex);
        } catch(Exception e) {
            throw new SQLException(e);
        }
        return builder.build();
    }

    /**
     *
     * @param rsmd
     * @param metadataBuilder
     * @param query
     * @param converter
     * @throws SQLException
     * @title Построение модели
     */
    private StoreMetadata buildModel(ResultSetMetaData rsmd, StoreMetadataBuilder metadataBuilder, Query query, TypeConverter converter) throws SQLException {
        ColumnMetadata[] generatedCols = FieldsHelper.generateColumns(rsmd, query, converter);
        Map<String, ColumnMetadata> modelMap = new HashMap<>();
        for(ColumnMetadata col : generatedCols) {
            ColumnMetadata old = modelMap.get(col.getName());
            if(old == null) {
                modelMap.put(col.getName(), col);
                metadataBuilder.addFields(col);
            }
        }
        return metadataBuilder.build();
    }
}
