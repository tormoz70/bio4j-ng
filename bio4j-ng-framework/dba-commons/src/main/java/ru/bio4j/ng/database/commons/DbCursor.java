package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.DelegateSQLAction;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.SrvcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class DbCursor extends DbCommand<SQLCursor> implements SQLCursor, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(DbCursor.class);


	private boolean isActive = false;

	private String sql = null;
    private SQLReader reader;

    public DbCursor() {
        this.setParamSetter(new DbSelectableParamSetter());
    }

	@Override
	public SQLCursor init(Connection conn, String sql, List<Param> params, int timeout) throws Exception {
        if(Strings.isNullOrEmpty(sql))
            throw new IllegalArgumentException("Parameter \"sql\" cannon be empty!!!");
        this.sql = sql;
		return super.init(conn, params, timeout);
	}

    @Override
    public SQLCursor init(Connection conn, String sql, List<Param> params) throws Exception {
        return this.init(conn, sql, params, 60);
    }

    @Override
    public SQLCursor init(Connection conn, String sql) throws Exception {
        return this.init(conn, sql, null, 60);
    }


    @Override
    public SQLCursor init(Connection conn, BioCursorDeclaration.SelectSQLDef sqlDef, int timeout) throws Exception {
        return this.init(conn, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration(), timeout);
    }

    @Override
    public SQLCursor init(Connection conn, BioCursorDeclaration.SelectSQLDef sqlDef) throws Exception {
        return this.init(conn, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration(), 60);
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
    public SQLReader createReader(ResultSet resultSet) {
        return new DbReader(resultSet);
    }

	@Override
	public SQLCursor open(List<Param> params, User usr) throws Exception {
        List<Param> prms = params != null ? params : new ArrayList<>();
        SrvcUtils.applyCurrentUserParams(usr, prms);
        return this.processStatement(params, new DelegateSQLAction() {
            @Override
            public void execute() throws SQLException {
                final DbCursor self = DbCursor.this;
                self.reader = createReader(self.preparedStatement.executeQuery());
                self.isActive = true;
            }
        });
	}

    @Override
    public SQLCursor open(User usr) throws Exception {
        return this.open(null, usr);
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
