package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.commons.utils.Lists;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SortingWrapper;
import ru.bio4j.ng.database.api.WrapperInterpreter;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.Sort;

import java.util.List;

public class SortingWrapperBaseImpl extends AbstractWrapper implements SortingWrapper {

    public static final String EXPRESSION = "sorting$expression";
    public static final String ORDER_BY_CLAUSE = "${ORDERBYCLAUSE_PLACEHOLDER}";

    private String queryPrefix;
    private String querySuffix;

    public SortingWrapperBaseImpl(String template, WrapperInterpreter wrapperInterpreter) {
        super(template, wrapperInterpreter);
    }

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

    public String wrap(String sql, List<Sort> sort, List<Field> fields) throws Exception {
        if (sort != null && sort.size() > 0) {

            for (Sort s : sort) {
                Field fldDef = Lists.first(fields, item -> Strings.compare(s.getFieldName(), item.getName(), true) || Strings.compare(s.getFieldName(), item.getAttrName(), true));
                if (fldDef != null) {
                    if (!Strings.isNullOrEmpty(fldDef.getSorter()))
                        s.setFieldName(fldDef.getSorter());
                    else
                        s.setFieldName(fldDef.getName());
                }
            }

            String orderbySql = wrapperInterpreter.sortToSQL("srtng$wrpr", sort);
            return queryPrefix + sql + querySuffix + (Strings.isNullOrEmpty(orderbySql) ? "" : " ORDER BY " + orderbySql);
        }
        return sql;
    }
}
