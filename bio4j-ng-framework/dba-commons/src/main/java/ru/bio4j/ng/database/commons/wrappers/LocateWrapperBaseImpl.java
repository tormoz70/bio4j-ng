package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.api.LocateWrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.service.api.RestParamNames;

import static ru.bio4j.ng.database.api.WrapQueryType.LOCATE;

public class LocateWrapperBaseImpl extends AbstractWrapper implements LocateWrapper {

    private String template;

    public LocateWrapperBaseImpl(String template) {
        super(template);
    }

    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    public BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef, Object location) throws Exception {
        if(location == null)
            return sqlDef;
        Field pkCol = sqlDef.findPk();
        if(pkCol == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", sqlDef.getBioCode()));
        String whereclause = "(" + pkCol.getName() + " = :" + RestParamNames.LOCATE_PARAM_PKVAL + ") AND (rnum$ >= :" + RestParamNames.LOCATE_PARAM_STARTFROM + ")";
        String sql = template.replace(QUERY, sqlDef.getPreparedSql());
        sql = sql.replace(WHERE_CLAUSE, whereclause);
        sqlDef.setLocateSql(sql);
        return sqlDef;
    }
}
