package ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.filtering;

import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.AbstractWrapper;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapperType;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.jstore.Column;
import ru.bio4j.ng.service.api.BioCursor;

import static ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType.GETROW;
import static ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType.LOCATE;

/**
 * Wrapper для реализации постраничной выборки данных
 */
@WrapperType(GETROW)
public class GetrowWrapper extends AbstractWrapper {

    private String template;
    public static final String PKVAL = "GETROW$PKVALUE";

    public GetrowWrapper(String template) {
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
        Column pkCol = cursor.findPk();
        if(pkCol == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getBioCode()));
        String whereclause = "(" + pkCol.getName() + " = :" + PKVAL + ")";
        String sql = template.replace(QUERY, cursor.getSql());
        sql = sql.replace(WHERE_CLAUSE, whereclause);
        cursor.setGetrowSql(sql);
        return cursor;
    }
}
