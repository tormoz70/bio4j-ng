package ru.bio4j.ng.database.doa.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.Param;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Засовывает значения из params в CallableStatement
 * User: ayrat
 * Date: 29.11.13
 * Time: 17:13
 */
public class OraCallableParamSetter implements OraParamSetter {
    private static final Logger LOG = LoggerFactory.getLogger(OraCallableParamSetter.class);

    private OraCommand owner;
    public OraCallableParamSetter(OraCommand owner) {
        this.owner = owner;
    }

    @Override
    public void setParamsToStatement(PreparedStatement statement, List<Param> params) throws SQLException {
        CallableStatement callable = (statement instanceof CallableStatement) ? (CallableStatement)statement : null;
        if(callable == null)
            throw new SQLException("Parameter [statement] mast be instance of CallableStatement!");
        final String sql = this.owner.getPreparedSQL();
        final List<String> paramsNames = OraUtils.extractParamNamesFromSQL(sql);
        final List<Param> outParams = new ArrayList<>();
        for (int i = 0; i < paramsNames.size(); i++) {
            String paramName = paramsNames.get(i);
            Param param = Paramus.set(params).getParam(paramName);
            if (param != null) {
                param.setId(i + 1);
                if ((param.getDirection() == Param.Direction.IN) || (param.getDirection() == Param.Direction.INOUT)) {
                    callable.setObject(paramName, param.getValue());
                }
                if ((param.getDirection() == Param.Direction.OUT) || (param.getDirection() == Param.Direction.INOUT)) {
                    outParams.add(param);
                }
            } else
                throw new IllegalArgumentException("Parameter "+paramName+" not defined in input Params!");
        }
        for (Param outParam : outParams) {
            int sqlType = OraUtils.paramSqlType(outParam);
            String paramName = outParam.getName();
            callable.registerOutParameter(paramName, sqlType);
        }
    }
}
