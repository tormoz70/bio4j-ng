package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.database.api.PaginationWrapper;
import ru.bio4j.ng.database.api.WrapperInterpreter;
import ru.bio4j.ng.database.commons.AbstractWrapper;

public class PaginationWrapperBaseImpl extends AbstractWrapper implements PaginationWrapper {

    private String template;

    public PaginationWrapperBaseImpl(String template, WrapperInterpreter wrapperInterpreter) {
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
