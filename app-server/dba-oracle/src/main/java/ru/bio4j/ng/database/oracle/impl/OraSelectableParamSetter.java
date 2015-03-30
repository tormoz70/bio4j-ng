package ru.bio4j.ng.database.oracle.impl;

import oracle.jdbc.OraclePreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Sqls;
import ru.bio4j.ng.database.api.NamedParametersStatement;
import ru.bio4j.ng.database.api.SQLCommand;
import ru.bio4j.ng.database.api.SQLParamSetter;
import ru.bio4j.ng.model.transport.Param;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Засовывает значения из params в OraclePreparedStatement
 */
public class OraSelectableParamSetter implements SQLParamSetter {
    private static final Logger LOG = LoggerFactory.getLogger(OraSelectableParamSetter.class);

    public OraSelectableParamSetter() {
    }

    @Override
    public void setParamsToStatement(SQLCommand command, List<Param> params) throws SQLException {
        OraclePreparedStatement selectable = (OraclePreparedStatement)command.getStatement();
        if(selectable == null)
            throw new SQLException("Parameter [statement] mast be instance of OraclePreparedStatement!");
        final String sql = command.getPreparedSQL();
        final List<String> paramsNames = Sqls.extractParamNamesFromSQL(sql);
        for (int i = 0; i < paramsNames.size(); i++) {
            String paramName = paramsNames.get(i);
            Param param = Paramus.set(params).getParam(paramName);
            if (param != null) {
                param.setId(i + 1);
                Object value = param.getValue();
                if(value != null)
                    selectable.setObjectAtName(paramName, value);
                else
                    selectable.setNullAtName(paramName, Types.NULL);
            } else
                selectable.setNullAtName(paramName, Types.NULL);
        }
    }
}
