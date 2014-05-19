package ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.pagination;

import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.AbstractWrapper;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapperType;
import ru.bio4j.ng.service.api.BioCursor;

import static ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType.PAGING;

/**
 * @title Реализация обработчика запроса для наиболее простого случая, когда обертку можно записать без модификации запроса
 * @author rad
 */
@WrapperType(PAGING)
public class PaginationWrapper extends AbstractWrapper {

    public static final String OFFSET = "PAGING$OFFSET";
    public static final String LAST = "PAGING$LAST";

    private String queryPrefix;
    private String querySuffix;

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
        int start = template.indexOf(QUERY);
        if(start < 0) {
            throw new IllegalArgumentException("Query: \"" + template + "\" is not contain "+QUERY);
        }
        int end = start + QUERY.length();
        queryPrefix = template.substring(0, start);
        querySuffix = template.substring(end);
    }

    /**
     * Оборачивает запрос в предварительно загруженный запрос для выборки страницы
     * для переданной конструктору СУБД
     * @title "Оборачивание" запроса в предварительно загруженный запрос для выборки страницы для переданной конструктору СУБД
     * @title "О"
     * @param cursor
     * @return "Обернутый" запрос
     */
    @Override
    public BioCursor wrap(BioCursor cursor) throws Exception {
        if((cursor.getWrapMode() & BioCursor.WrapMode.PAGING.code()) == BioCursor.WrapMode.PAGING.code()) {
            cursor.setPreparedSql(queryPrefix + cursor.getPreparedSql() + querySuffix);
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
