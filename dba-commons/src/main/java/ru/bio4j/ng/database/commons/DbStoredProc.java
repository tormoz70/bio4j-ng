package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.commons.utils.SrvcUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class DbStoredProc extends DbCommand<SQLStoredProc> implements SQLStoredProc {
    private static final Logger LOG = LoggerFactory.getLogger(DbStoredProc.class);

    private String storedProcName;

	@Override
	public SQLStoredProc init(Connection conn, UpdelexSQLDef sqlDef, int timeout) {
	    if(sqlDef != null)
            this.storedProcName = sqlDef.getPreparedSql();
        return super.init(conn, sqlDef, timeout);
	}
    @Override
    public SQLStoredProc init(Connection conn, UpdelexSQLDef sqlDef) {
        return this.init(conn, sqlDef, 60);
    }
    @Override
    public SQLStoredProc init(Connection conn, String storedProcName) {
        this.storedProcName = storedProcName;
        return this.init(conn, null, 60);
    }

    @Override
    public SQLStoredProc init(Connection conn, String storedProcName, List<Param> paramDeclaration) {
        this.params = Paramus.clone(paramDeclaration);
        return this.init(conn, storedProcName);
    }

    @Override
	protected void prepareStatement() {
	    try {
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
        } catch (SQLException e) {
	        throw BioSQLException.create(e);
        }
	}
	
    @Override
	public void execSQL(Object params, User usr, boolean stayOpened) {
        List<Param> prms = params != null ? DbUtils.decodeParams(params) : new ArrayList<>();
        SrvcUtils.applyCurrentUserParams(usr, prms);

        BioSQLException lastError = null;
        try {
            try {
                try {
                    this.resetCommand(); // Сбрасываем состояние

                    if (this.params == null)
                        this.params = new ArrayList<>();

                    if(!stayOpened)
                        prepareStatement();

                    applyInParamsToStatmentParams(prms, false);

                    if (!doBeforeStatement(this.params)) // Обрабатываем события
                        return;

                    setParamsToStatement(); // Применяем параметры

                    if(LOG.isDebugEnabled())
                        LOG.debug("Try to execute (autocommit: {}) : {}", this.preparedStatement.getConnection().getAutoCommit(), getSQL2Execute(this.preparedSQL, this.preparedStatement.getParamsAsString()));
                    preparedStatement.execute();

                    getParamsFromStatement(); // Вытаскиваем OUT-параметры

                    DbUtils.applyParamsToParams(DbUtils.findOUTParams(this.params), params, false, true, false);
                    if (this.params != null) {
                        for (Param p : prms) {
                            Param exists = Paramus.getParam(this.params, DbUtils.normalizeParamName(p.getName()));
                            if (exists != null && !exists.getName().equalsIgnoreCase(p.getName())
                                    && Utl.arrayContains(new Param.Direction[]{Param.Direction.INOUT, Param.Direction.OUT}, exists.getDirection()))
                                exists.setName(p.getName());
                        }
                    }
                } catch (SQLException e) {
                    lastError = DbUtils.getInstance().extractStoredProcAppErrorMessage(e);
                    if(lastError == null)
                        lastError = BioSQLException.create(String.format("%s:\n - %s", "Error on execute command.", getSQL2Execute(this.preparedSQL, this.preparedStatement.getParamsAsString())), e);
                    throw lastError;
                } catch (Exception e) {
                    lastError = DbUtils.getInstance().extractStoredProcAppErrorMessage(e);
                    if(lastError == null)
                        lastError = BioSQLException.create(String.format("%s:\n - %s", "Error on execute command.", getSQL2Execute(this.preparedSQL, this.params)), e);
                    throw lastError;
                }
            } finally {

                this.doAfterStatement(SQLCommandAfterEvent.Attributes.build( // Обрабатываем события
                        this.params, lastError
                ));
            }
        } finally {
            if(!stayOpened)
                this.close();
        }
	}

    @Override
    public void execSQL(Object params, User usr) {
        this.execSQL(params, usr, false);
    }

    @Override
    public void execSQL(User usr) {
        this.execSQL(null, usr);
    }

    @Override
    protected void applyInParamsToStatmentParams(List<Param> params, boolean overwriteType) {
        DbUtils.applyParamsToParams(params, this.params, false, false, overwriteType);
    }

    @Override
    public void close() {
        Statement statement = getStatement();
        if(statement != null)
            try {
                statement.close();
            } catch (SQLException ignore) {}
    }

}
