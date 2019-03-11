package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.database.api.GetrowWrapper;
import ru.bio4j.ng.database.api.WrapperInterpreter;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.service.api.RestParamNames;

public class GetrowWrapperBaseImpl extends AbstractWrapper implements GetrowWrapper {

    private String template;

    public GetrowWrapperBaseImpl(String template, WrapperInterpreter wrapperInterpreter) {
        super(template, wrapperInterpreter);
    }

    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    public String wrap(String sql, String pkFieldName) throws Exception {
//        if(Strings.isNullOrEmpty(pkFieldName))
//            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", sqlDef.getBioCode()));
        String whereclause = "(" + pkFieldName + " = :" + RestParamNames.GETROW_PARAM_PKVAL + ")";
        String rslt = template.replace(QUERY, sql);
        return rslt.replace(WHERE_CLAUSE, whereclause);
    }
}