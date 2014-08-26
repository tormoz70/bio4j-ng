package ru.bio4j.ng.database.doa.impl.wrappers.pagination;

import ru.bio4j.ng.database.doa.impl.wrappers.AbstractWrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.api.BioCursor;

import static ru.bio4j.ng.database.api.WrapQueryType.TOTALS;

/**
 * Wrapper для реализации постраничной выборки данных
 */
@WrapperType(TOTALS)
public class TotalsWrapper extends AbstractWrapper {

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
    public BioCursor wrap(BioCursor cursor) throws Exception {
        String sql = template.replace(QUERY, cursor.getPreparedSql());
        cursor.setTotalsSql(sql);
        return cursor;

    }
}
