package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.api.PaginationWrapper;
import ru.bio4j.ng.database.commons.AbstractWrapper;

public class PaginationWrapperBaseImpl extends AbstractWrapper implements PaginationWrapper {

    private String template;

    public PaginationWrapperBaseImpl(String template) {
        super(template);
    }

    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    public BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef, int pageSize) throws Exception {
        if(pageSize > 0) {
            if((sqlDef.getWrapMode() & BioCursorDeclaration.WrapMode.PAGINATION.code()) == BioCursorDeclaration.WrapMode.PAGINATION.code()) {
                String sql = template.replace(QUERY, sqlDef.getPreparedSql());
                sqlDef.setPreparedSql(sql);
            }
        }
        return sqlDef;
    }
}
