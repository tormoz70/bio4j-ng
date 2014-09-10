package ru.bio4j.ng.database.commons.wrappers.pagination;

import ru.bio4j.ng.database.api.Wrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.api.BioCursor;
import ru.bio4j.ng.database.commons.AbstractWrapper;

import static ru.bio4j.ng.database.api.WrapQueryType.TOTALS;

/**
 * Wrapper для реализации постраничной выборки данных
 */
@WrapperType(TOTALS)
public class TotalsWrapper extends AbstractWrapper implements Wrapper<BioCursor.SelectSQLDef> {

    private String template;

    public TotalsWrapper(String template) {
        super(template);
    }

    /**
     * @title Разбор запроса
     * @param template
     */
    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    /**
     * Собирает запрос для вычисления общего кол-ва записей
     */
    @Override
    public BioCursor.SelectSQLDef wrap(BioCursor.SelectSQLDef sqlDef) throws Exception {
        String sql = template.replace(QUERY, sqlDef.getPreparedSql());
        sqlDef.setTotalsSql(sql);
        return sqlDef;

    }
}
