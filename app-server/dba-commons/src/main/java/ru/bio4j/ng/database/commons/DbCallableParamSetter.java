package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Sqls;
import ru.bio4j.ng.database.api.NamedParametersStatement;
import ru.bio4j.ng.database.api.SQLCommand;
import ru.bio4j.ng.database.api.SQLParamSetter;
import ru.bio4j.ng.database.api.SqlTypeConverter;
import ru.bio4j.ng.model.transport.Param;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Засовывает значения из params в CallableStatement
 */
public class DbCallableParamSetter implements SQLParamSetter {
    private static final Logger LOG = LoggerFactory.getLogger(DbCallableParamSetter.class);

    private DbCommand owner;
    private SqlTypeConverter sqlTypeConverter = new SqlTypeConverterImpl();
    public DbCallableParamSetter(DbCommand owner) {
        this.owner = owner;
    }

    @Override
    public void setParamsToStatement(SQLCommand command, List<Param> params) throws SQLException {
        NamedParametersStatement callable = command.getStatement();
        final String sql = this.owner.getPreparedSQL();
        final List<String> paramsNames = Sqls.extractParamNamesFromSQL(sql);
        final List<Param> outParams = new ArrayList<>();
        try (Paramus p = Paramus.set(params)) {
            for (int i = 0; i < paramsNames.size(); i++) {
                String paramName = paramsNames.get(i);
                Param param = p.getParam(paramName);
                if (param != null) {
                    param.setId(i + 1);
                    if ((param.getDirection() == Param.Direction.IN) || (param.getDirection() == Param.Direction.INOUT)) {
                        Object val = param.getValue();
                        Class<?> valType = (val != null ? val.getClass() : null);
                        int sqlType = DbUtils.getInstance().paramSqlType(param);
                        String sqlTypeName = DbUtils.getInstance().getSqlTypeName(sqlType);
                        int charSize = 0;
                        if(valType == String.class)
                            charSize = ((String)val).length();
                        Class<?> targetValType = sqlTypeConverter.write(sqlType, charSize);
                        try {
                            val = (val != null) ? Converter.toType(val, targetValType) : val;
                        } catch (ConvertValueException e) {
                            throw new SQLException(String.format("Error cast parameter \"%s\", value \"%s\" from type: \"%s\" to type: \"%s\"",
                                    paramName, val, valType.getSimpleName(), targetValType.getSimpleName()), e);
                        }
                        try {
                            callable.setObjectAtName(paramName, val, sqlType);
                        } catch (SQLException e) {
                            throw new SQLException(String.format("Error on setting parameter \"%s\"(sqlType: %s) to value \"%s\"(type: %s)",
                                    paramName, sqlTypeName, val, valType.getSimpleName()), e);
                        }
                    }
                    if ((param.getDirection() == Param.Direction.OUT) || (param.getDirection() == Param.Direction.INOUT)) {
                        outParams.add(param);
                    }
                } else
                    throw new IllegalArgumentException("Parameter " + paramName + " not defined in input Params!");
            }
        }
        for (Param outParam : outParams) {
            int sqlType = DbUtils.getInstance().paramSqlType(outParam);
            String sqlTypeName = DbUtils.getInstance().getSqlTypeName(sqlType);
            String paramName = outParam.getName();
            callable.registerOutParameter(paramName, sqlType);
        }
    }
}
