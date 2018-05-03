package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.SQLStoredProc;
import ru.bio4j.ng.database.api.StoredProgMetadata;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.commons.utils.SrvcUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class DbStoredProc extends DbCommand<SQLStoredProc> implements SQLStoredProc {
    private static final Logger LOG = LoggerFactory.getLogger(DbStoredProc.class);

    private String storedProcName;

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
    public SQLStoredProc init(Connection conn, String storedProcName) throws Exception {
        return this.init(conn, storedProcName, null, 60);
    }
//    @Override
//    public SQLStoredProc init(Connection conn, BioCursorDeclaration.UpdelexSQLDef sqlDef) throws Exception {
//        return this.init(conn, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration());
//    }

    @Override
	protected void prepareStatement() throws Exception {
        if (this.params == null) {
            StoredProgMetadata sp = DbUtils.getInstance().detectStoredProcParamsAuto(this.storedProcName, this.connection, this.params);
            try (Paramus p = Paramus.set(sp.getParamDeclaration())) {
                p.apply(params, true);
                params = p.get();
            }
        }

        String signature = DbUtils.generateSignature(storedProcName, params);
        preparedSQL = String.format("{call %s}", signature);
        preparedStatement = DbNamedParametersStatement.prepareCall(this.connection, this.preparedSQL);
        preparedStatement.setQueryTimeout(this.timeout);
	}
	
    @Override
	public void execSQL(Object params, User usr, boolean stayOpened) throws Exception {
        List<Param> prms = params != null ? DbUtils.decodeParams(params) : new ArrayList<>();
        SrvcUtils.applyCurrentUserParams(usr, prms);
        this.processStatement(prms, () -> {
            final DbStoredProc self = DbStoredProc.this;
            self.preparedStatement.execute();
        });
        if(this.preparedStatement != null && !stayOpened)
            try{ this.preparedStatement.close(); } catch (Exception e) {};
	}

    @Override
    public void execSQL(Object params, User usr) throws Exception {
        this.execSQL(params, usr, false);
    }

    @Override
    public void execSQL(User usr) throws Exception {
        this.execSQL(null, usr);
    }

    @Override
    protected void applyInParamsToStatmentParams(List<Param> params) throws Exception {
        DbUtils.applyParamsToParams(params, this.params, false, false);
    }

}
