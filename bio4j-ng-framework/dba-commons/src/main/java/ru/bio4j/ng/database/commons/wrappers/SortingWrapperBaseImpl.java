package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.commons.utils.Lists;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SortingWrapper;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.Sort;

import java.util.List;

public class SortingWrapperBaseImpl extends AbstractWrapper implements SortingWrapper {

    public static final String EXPRESSION = "sorting$expression";
    public static final String ORDER_BY_CLAUSE = "${ORDERBYCLAUSE_PLACEHOLDER}";

    private String queryPrefix;
    private String querySuffix;

    public SortingWrapperBaseImpl(String template) {
        super(template);
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
//        if((sqlDef.getWrapMode() & BioCursorDeclaration.WrapMode.SORT.code()) == BioCursorDeclaration.WrapMode.SORT.code()) {
            if(sort != null && sort.size() > 0) {

//                List<Field> fields = sqlDef.getFields();
                for (Sort s : sort) {
                    Field fldDef = Lists.first(fields, item -> Strings.compare(s.getFieldName(), item.getName(), true));
                    if(fldDef != null && !Strings.isNullOrEmpty(fldDef.getSorter()))
                        s.setFieldName(fldDef.getSorter());
                }

                String orderbySql = wrapperInterpreter.sortToSQL("srtng$wrpr", sort);
//                sqlDef.setPreparedSql(queryPrefix + sqlDef.getPreparedSql() + querySuffix + (Strings.isNullOrEmpty(orderbySql) ? "" : " ORDER BY " + orderbySql));
                return queryPrefix + sql + querySuffix + (Strings.isNullOrEmpty(orderbySql) ? "" : " ORDER BY " + orderbySql);
            }
//        }
        return sql;
    }
}
