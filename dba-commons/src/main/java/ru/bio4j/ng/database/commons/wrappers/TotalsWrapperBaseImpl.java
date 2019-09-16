package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.database.api.TotalsWrapper;
import ru.bio4j.ng.database.api.WrapperInterpreter;
import ru.bio4j.ng.database.commons.AbstractWrapper;

public class TotalsWrapperBaseImpl extends AbstractWrapper implements TotalsWrapper {

    private String template;

    public TotalsWrapperBaseImpl(String template, WrapperInterpreter wrapperInterpreter) {
        super(template, wrapperInterpreter);
    }

    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    public String wrap(String sql) {
        return template.replace(QUERY, sql);
    }
}
