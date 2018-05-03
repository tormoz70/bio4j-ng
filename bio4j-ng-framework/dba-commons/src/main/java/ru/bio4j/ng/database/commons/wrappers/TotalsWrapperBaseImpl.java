package ru.bio4j.ng.database.commons.wrappers;

import ru.bio4j.ng.database.api.TotalsWrapper;
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

    public String wrap(String sql) throws Exception {
//        String sql = template.replace(QUERY, sqlDef.getPreparedSql());
//        sqlDef.setTotalsSql(sql);
//        return sqlDef;
        return template.replace(QUERY, sql);
    }
}
