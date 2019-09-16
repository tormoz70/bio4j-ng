package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.FilteringWrapper;
import ru.bio4j.ng.database.api.WrapperInterpreter;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.util.List;

public class FilteringWrapperBaseImpl extends AbstractWrapper implements FilteringWrapper {

    private String queryPrefix;
    private String querySuffix;

    public FilteringWrapperBaseImpl(String template, WrapperInterpreter wrapperInterpreter) {
        super(template, wrapperInterpreter);
    }

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

    public String wrap(String sql, Filter filter, List<Field> fields) {
        if(filter != null) {
            String whereSql = wrapperInterpreter.filterToSQL("fltrng$wrpr", filter, fields);
            return queryPrefix + sql + querySuffix + (Strings.isNullOrEmpty(whereSql) ? "" : " WHERE " + whereSql);
        }
        return sql;
    }
}
