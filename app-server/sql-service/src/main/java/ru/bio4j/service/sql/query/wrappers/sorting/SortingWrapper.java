package ru.bio4j.service.sql.query.wrappers.sorting;

import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.query.wrappers.AbstractWrapper;
import ru.bio4j.service.sql.query.wrappers.WrapperType;
import ru.bio4j.util.Strings;

import java.sql.SQLException;

import static ru.bio4j.service.sql.query.wrappers.WrapQueryType.SORTING;

/**
 * @title Реализация обработчика запроса для наиболее простого случая, когда обертку можно записать без модификации запроса
 * @author rad
 */
@WrapperType(SORTING)
public class SortingWrapper extends AbstractWrapper {

    public static final String ORDER_BY_CLAUSE = "$ORDERBYCLAUSE";

    private String queryPrefix;
    private String querySuffix;

    public SortingWrapper(String query) {
        super(query);
    }

    /**
     * @title Разбор запроса
     * @param query
     */
    @Override
    protected void parseQuery(String query){
        //ищем место куда встявляется запрос
        int queryStart = query.indexOf(QUERY);
        int orderbyStart = query.indexOf(ORDER_BY_CLAUSE);
        if(queryStart < 0)
            throw new IllegalArgumentException("Query: \"" + query + "\" is not contain "+QUERY);
        if(orderbyStart < 0)
            throw new IllegalArgumentException("Query: \"" + query + "\" is not contain "+ORDER_BY_CLAUSE);

        int queryEnd = queryStart + QUERY.length();
        queryPrefix = query.substring(0, queryStart);
        querySuffix = query.substring(queryEnd, orderbyStart - 1);
    }

    /**
     * @title "Оборачивание" запроса в предварительно загруженный запрос
     * @title "О"
     * @param src
     * @return "Обернутый" запрос
     */
    @Override
    public Query wrap(QueryContext context, Query src) throws SQLException {
        String orderbySql = context.getDB().getWrapperInterpreter().sortToSQL("srtngwrpr", src.getSort());
        src.setSql(queryPrefix + src.getSql() + querySuffix + (Strings.empty(orderbySql) ? "" : " ORDER BY "+orderbySql));
        return src;
    }
}
