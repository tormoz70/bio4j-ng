package ru.bio4j.ng.database.commons.wrappers.pagination;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.api.Wrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Field;

import java.util.List;

import static ru.bio4j.ng.database.api.WrapQueryType.LOCATE;

/**
 * Wrapper для реализации постраничной выборки данных
 */
@WrapperType(LOCATE)
public class LocateWrapper extends AbstractWrapper implements Wrapper<BioCursorDeclaration.SelectSQLDef> {

    private String template;
    public static final String PKVAL = "locate$pkvalue";
    public static final String STARTFROM = "locate$startfrom";

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
    public BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef, List<Param> params) throws Exception {
        Object location = Paramus.paramValue(params, PKVAL);
        if(location == null)
            return sqlDef;
        Field pkCol = sqlDef.findPk();
        if(pkCol == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", sqlDef.getBioCode()));
        String whereclause = "(" + pkCol.getName() + " = :" + PKVAL + ") AND (rnum$ >= :" + STARTFROM + ")";
        String sql = template.replace(QUERY, sqlDef.getPreparedSql());
        sql = sql.replace(WHERE_CLAUSE, whereclause);
        sqlDef.setLocateSql(sql);
        return sqlDef;
    }
}
