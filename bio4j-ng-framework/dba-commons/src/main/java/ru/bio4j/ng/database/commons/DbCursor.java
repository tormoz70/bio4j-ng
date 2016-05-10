package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.DelegateSQLAction;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.Param;

import java.sql.*;
import java.util.List;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class DbCursor extends DbCommand<SQLCursor> implements SQLCursor, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(DbCursor.class);


	private boolean isActive = false;

	private String sql = null;
    private SQLReader reader;

    public DbCursor(SQLContext context) {
        super(context);
        this.setParamSetter(new DbSelectableParamSetter());
    }

	@Override
	public SQLCursor init(Connection conn, String sql, List<Param> params, int timeout) throws SQLException {
        if(Strings.isNullOrEmpty(sql))
            throw new IllegalArgumentException("Parameter \"sql\" cannon be empty!!!");
        this.sql = sql;
		return super.init(conn, params, timeout);
	}

    @Override
    public SQLCursor init(Connection conn, String sql, List<Param> params) throws SQLException {
        return this.init(conn, sql, params, 60);
    }

    @Override
	protected void prepareStatement() throws SQLException {
        this.preparedSQL = this.sql;
        this.preparedStatement = DbNamedParametersStatement.prepareStatement(this.connection, this.preparedSQL);
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
        return this.processStatement(params, new DelegateSQLAction() {
            @Override
            public void execute() throws SQLException {
                final DbCursor self = DbCursor.this;
                self.reader = context.createReader(self.preparedStatement.executeQuery());
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
