package ru.bio4j.service.processing;

import ru.bio4j.model.transport.jstore.ColumnMetadata;
import ru.bio4j.model.transport.jstore.ColumnType;
import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.result.Column;
import ru.bio4j.service.sql.types.AbstractTypeHandler;
import ru.bio4j.service.sql.types.TypeConverter;
import ru.bio4j.service.sql.types.TypeHandlerWrapper;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FieldsHelper {

    public static ColumnMetadata[] generateColumns(ResultSetMetaData rsmd, Query query, TypeConverter converter) throws SQLException {
        final int cc = rsmd.getColumnCount();
        final List<ColumnMetadata> cols = new ArrayList<>(cc);
        ColumnMetadata col;
        for(int i = 1 ; i <= cc; ++i){
            col = new ColumnMetadata();
            final String name = rsmd.getColumnLabel(i);
            final Column column = query.getColumns(name);
            final TypeHandlerWrapper handler = converter.getHandler(i);
            if(parse(name, col, column, ((AbstractTypeHandler)handler.getTypeHandler()).getMetaType())) {
                cols.add(col);
            }
        }
        return cols.toArray(new ColumnMetadata[cols.size()]);
    }

    private static boolean parse(String field, ColumnMetadata col, Column column, String metaType) {
        if (column != null) {
            col.setTitle(column.getTitle());
            col.setVisible(column.is(Column.VISIBLE));
            col.setEditable(column.is(Column.EDITABLE));
            col.setRequired(column.is(Column.REQUIRED));
            col.setName(column.getField());
            col.setColumnType(ColumnType.valueOf(metaType.toUpperCase()));
            return true;
        } else {
            col.setTitle(field);
            col.setName(field);
            return true;
        }
    }
}
