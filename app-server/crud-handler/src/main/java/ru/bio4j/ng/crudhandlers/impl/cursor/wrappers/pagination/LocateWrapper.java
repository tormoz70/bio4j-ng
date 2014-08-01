package ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.pagination;

import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.AbstractWrapper;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapperType;
import ru.bio4j.ng.service.api.BioCursor;

import java.util.regex.Pattern;

import static ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType.LOCATE;
import static ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType.TOTALS;

/**
 * Wrapper для реализации постраничной выборки данных
 */
@WrapperType(LOCATE)
public class LocateWrapper extends AbstractWrapper {

    private String template;

    public LocateWrapper(String template) {
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
     * Собирает запрос для вычисления страницы в которой находится искомая запись
     */
    @Override
    public BioCursor wrap(BioCursor cursor) throws Exception {
        String sql = Regexs.replace(template, QUERY, cursor.getPreparedSql(), Pattern.MULTILINE + Pattern.LITERAL);
        cursor.setLocateSql(sql);
        return cursor;
    }
}
