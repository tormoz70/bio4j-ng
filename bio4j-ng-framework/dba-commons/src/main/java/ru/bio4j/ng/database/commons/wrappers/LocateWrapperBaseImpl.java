package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.database.api.LocateWrapper;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.service.api.RestParamNames;

public class LocateWrapperBaseImpl extends AbstractWrapper implements LocateWrapper {

    private String template;

    public LocateWrapperBaseImpl(String template) {
        super(template);
    }

    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    public String wrap(String sql, String pkFieldName) throws Exception {
//        Field pkCol = sqlDef.findPk();
//        if(pkCol == null)
//            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", sqlDef.getBioCode()));
        String whereclause = "(" + pkFieldName + " = :" + RestParamNames.LOCATE_PARAM_PKVAL + ") AND (rnum$ >= :" + RestParamNames.LOCATE_PARAM_STARTFROM + ")";
        String rslt = template.replace(QUERY, sql);
        return rslt.replace(WHERE_CLAUSE, whereclause);
    }
}
