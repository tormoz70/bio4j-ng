package ru.bio4j.ng.database.commons.wrappers.filtering;

import ru.bio4j.ng.database.api.Wrapper;
import ru.bio4j.ng.database.api.WrapperType;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.database.api.BioCursor;

import java.util.ArrayList;
import java.util.List;

import static ru.bio4j.ng.database.api.WrapQueryType.GETROW;

/**
 * Wrapper для реализации постраничной выборки данных
 */
@WrapperType(GETROW)
public class GetrowWrapper extends AbstractWrapper implements Wrapper<BioCursor.SelectSQLDef> {

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
    public BioCursor.SelectSQLDef wrap(BioCursor.SelectSQLDef sqlDef) throws Exception {
        Field pkCol = sqlDef.findPk();
        if(pkCol == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", sqlDef.getBioCode()));
        String whereclause = "(" + pkCol.getName() + " = :" + PKVAL + ")";
        String sql = template.replace(QUERY, sqlDef.getSql());
        sql = sql.replace(WHERE_CLAUSE, whereclause);
        sqlDef.setPreparedSql(sql);
        List<Param> prms = new ArrayList<>();
        prms.add(Param.builder().name(PKVAL).type(pkCol.getMetaType()).build());
        sqlDef.setParams(prms);
        return sqlDef;
    }
}
