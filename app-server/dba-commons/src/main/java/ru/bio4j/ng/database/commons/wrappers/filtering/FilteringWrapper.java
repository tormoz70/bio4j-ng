package ru.bio4j.ng.database.commons.wrappers.filtering;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.api.BioCursor;
import ru.bio4j.ng.database.commons.AbstractWrapper;

import static ru.bio4j.ng.database.api.WrapQueryType.FILTERING;

/**
 * @title Реализация обработчика запроса для наиболее простого случая, когда обертку можно записать без модификации запроса
 * @author rad
 */
@WrapperType(FILTERING)
public class FilteringWrapper extends AbstractWrapper {

    private String queryPrefix;
    private String querySuffix;

    public FilteringWrapper(String template) {
        super(template);
    }

    /**
     * @title Разбор запроса
     * @param template
     */
    @Override
    protected void parseTemplate(String template){
        //ищем место куда встявляется запрос
        int queryStart = template.indexOf(QUERY);
        int whereStart = template.indexOf(WHERE_CLAUSE);
        if(queryStart < 0)
            throw new IllegalArgumentException("Query: \"" + template + "\" is not contain "+QUERY);
        if(whereStart < 0)
            throw new IllegalArgumentException("Query: \"" + template + "\" is not contain "+WHERE_CLAUSE);

        int queryEnd = queryStart + QUERY.length();
        queryPrefix = template.substring(0, queryStart);
        querySuffix = template.substring(queryEnd, whereStart - 1);
    }

    /**
     * @title "Оборачивание" запроса в предварительно загруженный запрос
     * @param cursor
     * @return "Обернутый" запрос
     */
    @Override
    public BioCursor wrap(BioCursor cursor) throws Exception {
        if ((cursor.getWrapMode() & BioCursor.WrapMode.FILTER.code()) == BioCursor.WrapMode.FILTER.code()) {
            if(cursor.getFilter() != null) {
                String whereSql = wrapperInterpreter.filterToSQL("fltrng$wrpr", cursor.getFilter());
                cursor.setPreparedSql(queryPrefix + cursor.getPreparedSql() + querySuffix + (Strings.isNullOrEmpty(whereSql) ? "" : " WHERE " + whereSql));
            }
        }
        return cursor;
    }
}
