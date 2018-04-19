package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.api.GetrowWrapper;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.service.api.RestParamNames;

public class GetrowWrapperBaseImpl extends AbstractWrapper implements GetrowWrapper {

    private String template;

    public GetrowWrapperBaseImpl(String template) {
        super(template);
    }

    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    public BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef) throws Exception {
        Field pkCol = sqlDef.findPk();
        if(pkCol == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", sqlDef.getBioCode()));
        String whereclause = "(" + pkCol.getName() + " = :" + RestParamNames.GETROW_PARAM_PKVAL + ")";
        String sql = template.replace(QUERY, sqlDef.getSql());
        sql = sql.replace(WHERE_CLAUSE, whereclause);
        sqlDef.setPreparedSql(sql);
        return sqlDef;
    }
}
