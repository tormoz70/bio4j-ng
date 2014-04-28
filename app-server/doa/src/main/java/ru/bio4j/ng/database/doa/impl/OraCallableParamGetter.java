package ru.bio4j.ng.database.doa.impl;

import oracle.jdbc.OracleCallableStatement;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Вытаскивает OUT параметры из statement и засовывает их в params
 */
public class OraCallableParamGetter implements OraParamGetter {
    private OraCommand owner;
    public OraCallableParamGetter(OraCommand owner) {
        this.owner = owner;
    }

    public void getParamsFromStatement(OracleCallableStatement statement, List<Param> params) throws SQLException {
        for(Param param : params) {
            if (Utl.arrayContains(new Param.Direction[] {Param.Direction.INOUT, Param.Direction.OUT}, param.getDirection())) {
                String paramName = param.getName();
                Object outValue = statement.getObject(paramName);
                param.setValue(outValue);
            }

        }
    }
}
