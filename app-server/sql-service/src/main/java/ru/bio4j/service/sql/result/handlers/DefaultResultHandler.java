package ru.bio4j.service.sql.result.handlers;

import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.result.DefaultQueryResult;
import ru.bio4j.service.sql.result.QueryResult;
import ru.bio4j.service.sql.types.MetaTypeResolver;
import ru.bio4j.service.sql.types.TypeConverter;
import ru.bio4j.service.sql.types.TypeConverterBuilder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Обработчик результатов, обрабатывает ResultSet и возвращает QueryResult
 * @title Обработчик результатов
 * @author rad
 */
public final class DefaultResultHandler implements ResultHandler<QueryResult> {

    /**
     * @title Обработка результирующего набора данных выборки из базы данных
     * @param rs
     * @param query
     * @param context
     * @return Экземпляр класса результата запроса
     * @throws SQLException
     */
    @Override
    public QueryResult handle(ResultSet rs, Query query, QueryContext context) throws SQLException {
        final ResultSetMetaData rsmd = rs.getMetaData();
        final int colCount = rsmd.getColumnCount();//
        //индекс именованной колонки
        final HashMap<String, Integer> colToIndex= new HashMap<>();
        //список имен колонок, в порядке следования
        final String colNames[] = new String[colCount];
        //чтение имен колонок
        for(int i = 0 ; i < colCount ; ++i){
            String colName = rsmd.getColumnLabel(i + 1);//с единицы начинается
            colNames[i] = colName;
            colToIndex.put(colName, i);
        }
        //подготовка конверсии типов
        final TypeConverter converter =  TypeConverterBuilder.build(
            context.getDB().getTypeMapper(),
            context.get(MetaTypeResolver.class),
            rsmd,
            query.getResultTypes());
        //Чтение данных
        final List<Object[]> dataArr = new ArrayList<>();
        Object row[];//строка
        try{
            while(rs.next()){//по строке
                row = new Object[colCount];
                for(int i = 1; i <= colCount ;++i ) {
                    row[i - 1] = converter.read(rs, i, null, null);
                }
                dataArr.add(row);
            }
        } catch(Exception e) {
            throw new SQLException(e);
        }
        Object data[][] = new Object[dataArr.size()][colCount];
        return new DefaultQueryResult( dataArr.toArray(data), colNames, colToIndex);
    }
}
