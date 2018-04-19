package ru.bio4j.ng.database.commons.wrappers.pagination;

import ru.bio4j.ng.database.api.Wrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.Param;

import java.util.List;

import static ru.bio4j.ng.database.api.WrapQueryType.TOTALS;

/**
 * Wrapper для реализации постраничной выборки данных
 */
@WrapperType(TOTALS)
public class TotalsWrapper extends AbstractWrapper implements Wrapper<BioCursorDeclaration.SelectSQLDef> {

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
    public BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef, List<Param> params) throws Exception {
        String sql = template.replace(QUERY, sqlDef.getPreparedSql());
        sqlDef.setTotalsSql(sql);
        return sqlDef;

    }
}
