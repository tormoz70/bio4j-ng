package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.database.api.TotalsWrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.commons.AbstractWrapper;

public class TotalsWrapperBaseImpl extends AbstractWrapper implements TotalsWrapper {

    private String template;

    public TotalsWrapperBaseImpl(String template) {
        super(template);
    }

    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    public BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef) throws Exception {
        String sql = template.replace(QUERY, sqlDef.getPreparedSql());
        sqlDef.setTotalsSql(sql);
        return sqlDef;

    }
}
