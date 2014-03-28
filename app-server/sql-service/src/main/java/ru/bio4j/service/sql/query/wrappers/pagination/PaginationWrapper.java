package ru.bio4j.service.sql.query.wrappers.pagination;

import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.query.wrappers.AbstractWrapper;
import ru.bio4j.service.sql.query.wrappers.WrapperType;

import static ru.bio4j.service.sql.query.wrappers.WrapQueryType.PAGING;

/**
 * @title Реализация обработчика запроса для наиболее простого случая, когда обертку можно записать без модификации запроса
 * @author rad
 */
@WrapperType(PAGING)
public class PaginationWrapper extends AbstractWrapper {

    public static final String OFFSET = "PAGING_OFFSET";
    public static final String LAST = "PAGING_LAST";
    public static final String COUNT = "PAGING_COUNT";

    private String queryPrefix;
    private String querySuffix;

    public PaginationWrapper(String query) {
        super(query);
    }

    /**
     * @title Разбор запроса
     * @param query
     */
    @Override
    protected void parseQuery(String query){
        //ищем место куда встявляется запрос
        int start = query.indexOf(QUERY);
        if(start < 0) {
            throw new IllegalArgumentException("Query: \"" + query + "\" is not contain "+QUERY);
        }
        int end = start + QUERY.length();
        queryPrefix = query.substring(0, start);
        querySuffix = query.substring(end);
    }

    /**
     * Оборачивает запрос в предварительно загруженный запрос для выборки страницы
     * для переданной конструктору СУБД
     * @title "Оборачивание" запроса в предварительно загруженный запрос для выборки страницы для переданной конструктору СУБД
     * @title "О"
     * @param src
     * @return "Обернутый" запрос
     */
    @Override
    public Query wrap(QueryContext context, Query src) {
        src.setSql(queryPrefix + src.getSql() + querySuffix);
        int offset = src.getOffset();
        int count = src.getCount();
        src.setParamValue(OFFSET, offset)
                .setParamValue(COUNT, count)
                .setParamValue(LAST, count + offset);
        return src;
    }
}
