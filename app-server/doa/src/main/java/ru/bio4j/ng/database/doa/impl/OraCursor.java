package ru.bio4j.ng.database.doa.impl;

import oracle.jdbc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.DelegateSQLAction;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.Param;

import java.sql.*;
import java.util.List;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class OraCursor extends OraCommand<SQLCursor> implements SQLCursor {
    private static final Logger LOG = LoggerFactory.getLogger(OraCursor.class);


	private boolean isActive = false;

	private String sql = null;
    private SQLReader reader;

    public OraCursor() {
	}

	@Override
	public SQLCursor init(Connection conn, String sql, List<Param> params, int timeout) throws SQLException {
        this.sql = sql;
		return super.init(conn, params, timeout);
	}

    @Override
    public SQLCursor init(Connection conn, String sql, List<Param> params) throws SQLException {
        return this.init(conn, sql, params, 60);
    }

    @Override
	protected void prepareStatement() throws SQLException {
        this.preparedSQL = (this.sqlWrapper != null) ? this.sqlWrapper.prepare(this.sql) : this.sql;
        this.preparedStatement = (OraclePreparedStatement)this.connection.prepareStatement(this.preparedSQL, ResultSet.TYPE_FORWARD_ONLY);
        this.preparedStatement.setQueryTimeout(this.timeout);
	}


    @Override
    protected void resetCommand() throws SQLException {
        super.resetCommand();
        if (this.isActive()){
            try {
                this.close();
            } catch (Exception e) {}
        }
    }

	@Override
	public SQLCursor open(List<Param> params) throws Exception {
        return (SQLCursor)this.processStatement(params, new DelegateSQLAction() {
            @Override
            public void execute() throws SQLException {
                final OraCursor self = OraCursor.this;
                self.reader = new OraReader(self.preparedStatement.executeQuery());
                self.isActive = true;

            }
        });
	}

    @Override
    public SQLCursor open() throws Exception {
        return this.open(null);
    }

	@Override
	public boolean isActive() {
		return this.isActive;
	}

    @Override
    public SQLReader reader() {
        return this.reader;
    }

    @Override
	public String getSQL() {
		return this.sql;
	}


    public void setSqlWrapper(SQLWrapper sqlWrapper) {
        this.sqlWrapper = sqlWrapper;
    }

    @Override
    public void close() throws Exception {
        this.isActive = false;
        this.cancel();
        final Statement stmnt = this.getStatement();
        if(stmnt != null)
            stmnt.close();

        if(this.reader != null) {
            this.reader.close();
            this.reader = null;
        }

    }
}
