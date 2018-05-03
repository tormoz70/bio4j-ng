package ru.bio4j.ng.database.commons.wrappers;

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

    public String wrap(String sql) throws Exception {
//        if((sqlDef.getWrapMode() & BioCursorDeclaration.WrapMode.PAGINATION.code()) == BioCursorDeclaration.WrapMode.PAGINATION.code()) {
            return template.replace(QUERY, sql);
//            sqlDef.setPreparedSql(sql);
//        }
//        return sqlDef;
    }
}
