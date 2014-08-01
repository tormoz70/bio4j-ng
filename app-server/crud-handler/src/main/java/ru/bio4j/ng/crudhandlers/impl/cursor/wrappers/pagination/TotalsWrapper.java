package ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.pagination;

import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.AbstractWrapper;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapperType;
import ru.bio4j.ng.service.api.BioCursor;

import java.util.regex.Pattern;

import static ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType.PAGING;
import static ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType.TOTALS;

/**
 * Wrapper для реализации постраничной выборки данных
 */
@WrapperType(TOTALS)
public class TotalsWrapper extends AbstractWrapper {

    private String template;
//    private String queryPrefix;
//    private String querySuffix;

    public TotalsWrapper(String template) {
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
     * Собирает запрос для вычисления общего кол-ва записей
     */
    @Override
    public BioCursor wrap(BioCursor cursor) throws Exception {
        String sql = Regexs.replace(template, QUERY, cursor.getPreparedSql(), Pattern.MULTILINE+Pattern.LITERAL);
        cursor.setTotalsSql(sql);
        return cursor;

    }
}
