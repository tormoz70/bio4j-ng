package ru.bio4j.ng.database.commons.wrappers.pagination;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.Wrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.Param;

import java.util.List;

import static ru.bio4j.ng.database.api.WrapQueryType.PAGINATION;

/**
 * Wrapper для реализации построничной выборки
 */
@WrapperType(PAGINATION)
public class PaginationWrapper extends AbstractWrapper implements Wrapper<BioCursorDeclaration.SelectSQLDef> {

    public static final String PAGINATION_OFFSET = "pagination$offset";
    public static final String PAGINATION_PAGE_SIZE = "pagination$pagesize";

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
    public BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef, List<Param> params) throws Exception {
        int pageSize = (int)Paramus.paramValue(params, PAGINATION_PAGE_SIZE);
        if(pageSize > 0) {
            if((sqlDef.getWrapMode() & BioCursorDeclaration.WrapMode.PAGINATION.code()) == BioCursorDeclaration.WrapMode.PAGINATION.code()) {
                String sql = template.replace(QUERY, sqlDef.getPreparedSql());
                sqlDef.setPreparedSql(sql);
            }
//            int offset = sqlDef.getOffset();
//            sqlDef.setParamValue(OFFSET, offset)
//                  .setParamValue(LAST, offset + pageSize);
        }
        return sqlDef;
    }
}
