package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.BioSQLException;
import ru.bio4j.ng.database.api.SQLNamedParametersStatement;
import ru.bio4j.ng.database.api.SQLParamGetter;
import ru.bio4j.ng.model.transport.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Вытаскивает OUT параметры из statement и засовывает их в params
 */
public class DbCallableParamGetter implements SQLParamGetter {
//    private DbCommand owner;
//    public DbCallableParamGetter(DbCommand owner) {
//        this.owner = owner;
//    }

    public void getParamsFromStatement(SQLNamedParametersStatement statment, List<Param> params) throws SQLException {
//        CallableStatement callable = (command.getStatement() instanceof CallableStatement) ? (CallableStatement)command.getStatement() : null;
//        SQLNamedParametersStatement callable = command.getStatement();
        if(params != null && params.size() > 0) {
            if (statment == null)
                throw new BioSQLException("Parameter [statement] mast be instance of CallableStatement!");
            for (Param param : params) {
                if (Utl.arrayContains(new Param.Direction[]{Param.Direction.INOUT, Param.Direction.OUT}, param.getDirection())) {
                    String paramName = param.getName();
                    Object outValue = statment.getObject(paramName);
                    param.setValue(outValue);
                }

            }
        }
    }
}
