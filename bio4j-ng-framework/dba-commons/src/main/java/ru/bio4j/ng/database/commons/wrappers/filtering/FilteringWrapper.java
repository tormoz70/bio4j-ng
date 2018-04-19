package ru.bio4j.ng.database.commons.wrappers.filtering;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.api.Wrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.util.List;

import static ru.bio4j.ng.database.api.WrapQueryType.FILTERING;

/**
 * @title Реализация обработчика запроса для наиболее простого случая, когда обертку можно записать без модификации запроса
 * @author rad
 */
@WrapperType(FILTERING)
public class FilteringWrapper extends AbstractWrapper implements Wrapper<BioCursorDeclaration.SelectSQLDef> {

    public static final String EXPRESSION = "filtering$expression";
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
     * @param sqlDef
     * @return "Обернутый" запрос
     */
    @Override
    public BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef, List<Param> params) throws Exception {
        Filter filter = (Filter)Paramus.paramValue(params, EXPRESSION);
        if ((sqlDef.getWrapMode() & BioCursorDeclaration.WrapMode.FILTER.code()) == BioCursorDeclaration.WrapMode.FILTER.code()) {
            if(filter != null) {
                String whereSql = wrapperInterpreter.filterToSQL("fltrng$wrpr", filter);
                sqlDef.setPreparedSql(queryPrefix + sqlDef.getPreparedSql() + querySuffix + (Strings.isNullOrEmpty(whereSql) ? "" : " WHERE " + whereSql));
            }
        }
        return sqlDef;
    }
}
