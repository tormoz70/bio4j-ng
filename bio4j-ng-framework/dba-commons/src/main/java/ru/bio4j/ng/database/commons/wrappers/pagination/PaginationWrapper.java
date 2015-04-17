package ru.bio4j.ng.database.commons.wrappers.pagination;

import ru.bio4j.ng.database.api.Wrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.api.BioCursor;
import ru.bio4j.ng.database.commons.AbstractWrapper;

import static ru.bio4j.ng.database.api.WrapQueryType.PAGING;

/**
 * Wrapper для реализации построничной выборки
 */
@WrapperType(PAGING)
public class PaginationWrapper extends AbstractWrapper implements Wrapper<BioCursor.SelectSQLDef> {

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
    public BioCursor.SelectSQLDef wrap(BioCursor.SelectSQLDef sqlDef) throws Exception {
        int pageSize = sqlDef.getPageSize();
        if(pageSize > 0) {
            if((sqlDef.getWrapMode() & BioCursor.WrapMode.PAGING.code()) == BioCursor.WrapMode.PAGING.code()) {
                String sql = template.replace(QUERY, sqlDef.getPreparedSql());
                sqlDef.setPreparedSql(sql);
            }
            int offset = sqlDef.getOffset();
            sqlDef.setParamValue(OFFSET, offset)
                  .setParamValue(LAST, offset + pageSize);
        }
        return sqlDef;
    }
}
