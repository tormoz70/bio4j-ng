package ru.bio4j.ng.database.doa.impl;

import oracle.jdbc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.DelegateSQLAction;
import ru.bio4j.ng.database.api.SQLStoredProc;
import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class OraStoredProc extends OraCommand<SQLStoredProc> implements SQLStoredProc {
    private static final Logger LOG = LoggerFactory.getLogger(OraStoredProc.class);

    private String storedProcName;

    public OraStoredProc() {
	}

	@Override
	public SQLStoredProc init(Connection conn, String storedProcName, List<Param> params, int timeout) throws SQLException {
        this.storedProcName = storedProcName;
		return super.init(conn, params, timeout);
	}
    @Override
    public SQLStoredProc init(Connection conn, String storedProcName, List<Param> params) throws SQLException {
        return this.init(conn, storedProcName, params, 60);
    }

    @Override
	protected void prepareStatement() throws SQLException {
        this.preparedSQL = OraUtils.detectStoredProcParamsAuto(this.storedProcName, this.connection);
        this.preparedSQL = "{call " + this.preparedSQL + "}";
        this.preparedStatement = this.connection.prepareCall(this.preparedSQL);
        this.preparedStatement.setQueryTimeout(this.timeout);
	}
	
    @Override
	public void execSQL(List<Param> params) throws SQLException {
        this.processStatement(params, new DelegateSQLAction() {
            @Override
            public void execute() throws SQLException {
                final OraStoredProc self = OraStoredProc.this;
                self.preparedStatement.execute();
            }
        });
	}

    @Override
    public void execSQL() throws SQLException {
        this.execSQL(null);
    }


}
