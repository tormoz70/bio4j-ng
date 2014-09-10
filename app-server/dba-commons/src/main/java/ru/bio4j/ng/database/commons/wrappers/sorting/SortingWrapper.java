package ru.bio4j.ng.database.commons.wrappers.sorting;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.Wrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.api.BioCursor;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.jstore.Sort;

import java.util.List;

import static ru.bio4j.ng.database.api.WrapQueryType.SORTING;

/**
 * @title Реализация обработчика запроса для наиболее простого случая, когда обертку можно записать без модификации запроса
 * @author rad
 */
@WrapperType(SORTING)
public class SortingWrapper extends AbstractWrapper implements Wrapper<BioCursor.SelectSQLDef> {

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
     * @param sqlDef
     * @return "Обернутый" запрос
     */
    @Override
    public BioCursor.SelectSQLDef wrap(BioCursor.SelectSQLDef sqlDef) throws Exception {
        if((sqlDef.getWrapMode() & BioCursor.WrapMode.SORT.code()) == BioCursor.WrapMode.SORT.code()) {
            List<Sort> sort = sqlDef.getSort();
            if(sort != null && sort.size() > 0) {
                String orderbySql = wrapperInterpreter.sortToSQL("srtng$wrpr", sqlDef.getSort());
                sqlDef.setPreparedSql(queryPrefix + sqlDef.getPreparedSql() + querySuffix + (Strings.isNullOrEmpty(orderbySql) ? "" : " ORDER BY " + orderbySql));
            }
        }
        return sqlDef;
    }
}
