package ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.pagination;

import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.AbstractWrapper;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapperType;
import ru.bio4j.ng.service.api.BioCursor;

import java.util.regex.Pattern;

import static ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType.PAGING;

/**
 * Wrapper для реализации построничной выборки
 */
@WrapperType(PAGING)
public class PaginationWrapper extends AbstractWrapper {

    public static final String OFFSET = "PAGING$OFFSET";
    public static final String LAST = "PAGING$LAST";

    private String template;
//    private String queryPrefix;
//    private String querySuffix;

    public PaginationWrapper(String template) {
        super(template);
    }

    /**
     * @title Разбор запроса
     * @param template
     */
    @Override
    protected void parseTemplate(String template){
        //ищем место куда встявляется запрос
//        int start = template.indexOf(QUERY);
//        if(start < 0) {
//            throw new IllegalArgumentException("Query: \"" + template + "\" is not contain "+QUERY);
//        }
//        int end = start + QUERY.length();
//        queryPrefix = template.substring(0, start);
//        querySuffix = template.substring(end);
        this.template = template;
    }

    /**
     * Создает запрос для постраничнеой выборки данных
     */
    @Override
    public BioCursor wrap(BioCursor cursor) throws Exception {
        if((cursor.getWrapMode() & BioCursor.WrapMode.PAGING.code()) == BioCursor.WrapMode.PAGING.code()) {
            String sql = Regexs.replace(template, QUERY, cursor.getPreparedSql(), Pattern.MULTILINE + Pattern.LITERAL);
//            cursor.setPreparedSql(queryPrefix + cursor.getPreparedSql() + querySuffix);
            cursor.setPreparedSql(sql);
        }
        int pageSize = cursor.getPageSize();
        if(pageSize > 0) {
            int offset = cursor.getOffset();
            cursor.setParamValue(OFFSET, offset)
                  .setParamValue(LAST, pageSize + offset);
        }
        return cursor;
    }
}
