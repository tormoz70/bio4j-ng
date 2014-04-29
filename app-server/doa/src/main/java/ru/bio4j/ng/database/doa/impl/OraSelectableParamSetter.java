package ru.bio4j.ng.database.doa.impl;

//import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OraclePreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.Param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Засовывает значения из params в OraclePreparedStatement
 * User: ayrat
 * Date: 29.11.13
 * Time: 17:13
 */
public class OraSelectableParamSetter implements OraParamSetter {
    private static final Logger LOG = LoggerFactory.getLogger(OraSelectableParamSetter.class);

    private OraCommand owner;
    public OraSelectableParamSetter(OraCommand owner) {
        this.owner = owner;
    }

    @Override
    public void setParamsToStatement(PreparedStatement statement, List<Param> params) throws SQLException {
        OraclePreparedStatement selectable = (statement instanceof OraclePreparedStatement) ? (OraclePreparedStatement)statement : null;
        if(selectable == null)
            throw new SQLException("Parameter [statement] mast be instance of OraclePreparedStatement!");
        final String sql = this.owner.getPreparedSQL();
        final List<String> paramsNames = OraUtils.extractParamNamesFromSQL(sql);
        for (int i = 0; i < paramsNames.size(); i++) {
            String paramName = paramsNames.get(i);
            Param param = Paramus.set(params).getParam(paramName);
            if (param != null) {
                param.setId(i + 1);
                selectable.setObjectAtName(paramName, param.getValue());
            } else
                selectable.setNullAtName(paramName, Types.NULL);
        }
    }
}
