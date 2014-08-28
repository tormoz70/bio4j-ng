package ru.bio4j.ng.database.commons.wrappers.pagination;

import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.jstore.Column;
import ru.bio4j.ng.database.api.BioCursor;

import static ru.bio4j.ng.database.api.WrapQueryType.LOCATE;

/**
 * Wrapper для реализации постраничной выборки данных
 */
@WrapperType(LOCATE)
public class LocateWrapper extends AbstractWrapper {

    private String template;
    public static final String PKVAL = "LOCATE$PKVALUE";
    public static final String STARTFROM = "LOCATE$STARTFROM";

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
        if(cursor.getLocation() == null)
            return cursor;
        Column pkCol = cursor.findPk();
        if(pkCol == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getBioCode()));
        String whereclause = "(" + pkCol.getName() + " = :" + PKVAL + ") AND (rnum$ >= :" + STARTFROM + ")";
        String sql = template.replace(QUERY, cursor.getPreparedSql());
        sql = sql.replace(WHERE_CLAUSE, whereclause);
        cursor.setLocateSql(sql);
        return cursor;
    }
}
