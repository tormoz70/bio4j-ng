package ru.bio4j.ng.database.doa.impl.wrappers.sorting;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.doa.impl.wrappers.AbstractWrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.api.BioCursor;

import static ru.bio4j.ng.database.api.WrapQueryType.SORTING;

/**
 * @title Реализация обработчика запроса для наиболее простого случая, когда обертку можно записать без модификации запроса
 * @author rad
 */
@WrapperType(SORTING)
public class SortingWrapper extends AbstractWrapper {

    public static final String ORDER_BY_CLAUSE = "${ORDERBYCLAUSE_PLACEHOLDER}";

    private String queryPrefix;
    private String querySuffix;

    public SortingWrapper(String template) {
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
        int orderbyStart = template.indexOf(ORDER_BY_CLAUSE);
        if(queryStart < 0)
            throw new IllegalArgumentException("Query: \"" + template + "\" is not contain "+QUERY);
        if(orderbyStart < 0)
            throw new IllegalArgumentException("Query: \"" + template + "\" is not contain "+ORDER_BY_CLAUSE);

        int queryEnd = queryStart + QUERY.length();
        queryPrefix = template.substring(0, queryStart);
        querySuffix = template.substring(queryEnd, orderbyStart - 1);
    }

    /**
     * @title "Оборачивание" запроса в предварительно загруженный запрос
     * @title "О"
     * @param cursor
     * @return "Обернутый" запрос
     */
    @Override
    public BioCursor wrap(BioCursor cursor) throws Exception {
        if((cursor.getWrapMode() & BioCursor.WrapMode.SORT.code()) == BioCursor.WrapMode.SORT.code()) {
            if(cursor.getSort() != null) {
                String orderbySql = wrapperInterpreter.sortToSQL("srtng$wrpr", cursor.getSort());
                cursor.setPreparedSql(queryPrefix + cursor.getPreparedSql() + querySuffix + (Strings.isNullOrEmpty(orderbySql) ? "" : " ORDER BY " + orderbySql));
            }
        }
        return cursor;
    }
}
