package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.DelegateSQLAction;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLStoredProc;
import ru.bio4j.ng.database.api.StoredProgMetadata;
import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class DbStoredProc extends DbCommand<SQLStoredProc> implements SQLStoredProc {
    private static final Logger LOG = LoggerFactory.getLogger(DbStoredProc.class);

    private String storedProcName;

    public DbStoredProc(SQLContext context) {
        super(context);
    }

	@Override
	public SQLStoredProc init(Connection conn, String storedProcName, Object params, int timeout) throws Exception {
        this.storedProcName = storedProcName;
        List<Param> prms = DbUtils.decodeParams(params);
        return super.init(conn, prms, timeout);
	}
    @Override
    public SQLStoredProc init(Connection conn, String storedProcName, Object params) throws Exception {
        return this.init(conn, storedProcName, params, 60);
    }

    @Override
	protected void prepareStatement() throws SQLException {
        StoredProgMetadata sp = DbUtils.getInstance().detectStoredProcParamsAuto(this.storedProcName, this.connection, this.params);

        try(Paramus p = Paramus.set(sp.getParams())){
            p.apply(params, true);
            params = p.get();
        }
        preparedSQL = String.format("{call %s}", sp.getSignature());
        preparedStatement = DbNamedParametersStatement.prepareCall(this.connection, this.preparedSQL);
        preparedStatement.setQueryTimeout(this.timeout);
	}
	
    @Override
	public void execSQL(Object params) throws Exception {
        List<Param> prms = DbUtils.decodeParams(params);
        this.processStatement(prms, new DelegateSQLAction() {
            @Override
            public void execute() throws SQLException {
                final DbStoredProc self = DbStoredProc.this;
                self.preparedStatement.execute();
            }
        });
        if(this.preparedStatement != null)
            try{ this.preparedStatement.close(); } catch (Exception e) {};
	}

    @Override
    public void execSQL() throws Exception {
        this.execSQL(null);
    }


}
