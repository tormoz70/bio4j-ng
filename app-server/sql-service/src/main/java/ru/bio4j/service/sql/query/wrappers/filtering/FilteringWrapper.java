package ru.bio4j.service.sql.query.wrappers.filtering;

import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.query.wrappers.AbstractWrapper;
import ru.bio4j.service.sql.query.wrappers.WrapperType;
import ru.bio4j.util.Strings;

import java.sql.SQLException;

import static ru.bio4j.service.sql.query.wrappers.WrapQueryType.FILTERING;

/**
 * @title Реализация обработчика запроса для наиболее простого случая, когда обертку можно записать без модификации запроса
 * @author rad
 */
@WrapperType(FILTERING)
public class FilteringWrapper extends AbstractWrapper {

    public static final String WHERE_CLAUSE = "$WHERECLAUSE";

    private String queryPrefix;
    private String querySuffix;

    public FilteringWrapper(String query) {
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
        int whereStart = query.indexOf(WHERE_CLAUSE);
        if(queryStart < 0)
            throw new IllegalArgumentException("Query: \"" + query + "\" is not contain "+QUERY);
        if(whereStart < 0)
            throw new IllegalArgumentException("Query: \"" + query + "\" is not contain "+WHERE_CLAUSE);

        int queryEnd = queryStart + QUERY.length();
        queryPrefix = query.substring(0, queryStart);
        querySuffix = query.substring(queryEnd, whereStart - 1);
    }

    /**
     * @title "Оборачивание" запроса в предварительно загруженный запрос
     * @param src
     * @return "Обернутый" запрос
     */
    @Override
    public Query wrap(QueryContext context, Query src) throws SQLException {
        String whereSql = context.getDB().getWrapperInterpreter().filterToSQL("fltrngwrpr", src.getFilter());
        src.setSql(queryPrefix + src.getSql() + querySuffix + (Strings.empty(whereSql) ? "" : " WHERE "+whereSql));
        return src;
    }
}
