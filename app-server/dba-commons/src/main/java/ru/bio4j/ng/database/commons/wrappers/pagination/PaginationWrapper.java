package ru.bio4j.ng.database.commons.wrappers.pagination;

import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.api.BioCursor;
import ru.bio4j.ng.database.commons.AbstractWrapper;

import static ru.bio4j.ng.database.api.WrapQueryType.PAGING;

/**
 * Wrapper для реализации построничной выборки
 */
@WrapperType(PAGING)
public class PaginationWrapper extends AbstractWrapper {

    public static final String OFFSET = "PAGING$OFFSET";
    public static final String LAST = "PAGING$LAST";

    private String template;

    public PaginationWrapper(String template) {
        super(template);
    }

    /**
     * @title Разбор запроса
     * @param template
     */
    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    /**
     * Создает запрос для постраничнеой выборки данных
     */
    @Override
    public BioCursor wrap(BioCursor cursor) throws Exception {
        int pageSize = cursor.getPageSize();
        if(pageSize > 0) {
            if((cursor.getWrapMode() & BioCursor.WrapMode.PAGING.code()) == BioCursor.WrapMode.PAGING.code()) {
                String sql = template.replace(QUERY, cursor.getPreparedSql());
                cursor.setPreparedSql(sql);
            }
            int offset = cursor.getOffset();
            cursor.setParamValue(OFFSET, offset)
                  .setParamValue(LAST, offset + pageSize);
        }
        return cursor;
    }
}
