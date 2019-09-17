package ru.bio4j.ng.database.commons;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import javax.sql.DataSource;

public abstract class DbContextAbstract implements SQLContext {
    private static final Logger LOG = LoggerFactory.getLogger(DbContextAbstract.class);

    protected Wrappers wrappers;
    protected DataSource cpool;

    protected final List<SQLConnectionConnectedEvent> afterEvents = new ArrayList<>();
    protected final List<SQLConnectionConnectedEvent> innerAfterEvents = new ArrayList<>();

    protected final SQLConnectionPoolConfig config;

    protected DbContextAbstract(final DataSource cpool, final SQLConnectionPoolConfig config) throws Exception {
        this.cpool = cpool;
        this.config = config;
    }

    protected User user;
    protected Connection connection;

    public User getCurrentUser() {
        return ThreadContextHolder.instance().getCurrentUser();
    }

    public Connection getCurrentConnection() {
        return ThreadContextHolder.instance().getCurrentConnection();
    }

    public SQLReader createReader(){
        return new DbReader();
    }

    private void setCurrentContext(User user, Connection conn) {
        ThreadContextHolder.instance().setContext(user, conn, this);
    }

    private void closeCurrentContext() {
        ThreadContextHolder.instance().close();
    }

    @Override
    public void addAfterEvent(SQLConnectionConnectedEvent e) {
        this.afterEvents.add(e);
    }

    @Override
    public void clearAfterEvents() {
        this.afterEvents.clear();
    }

    protected void doAfterConnect(SQLConnectionConnectedEvent.Attributes attrs) throws SQLException {
        if (this.innerAfterEvents.size() > 0) {
            for (SQLConnectionConnectedEvent e : this.innerAfterEvents)
                e.handle(this, attrs);
        }
        if (this.afterEvents.size() > 0) {
            for (SQLConnectionConnectedEvent e : this.afterEvents)
                e.handle(this, attrs);
        }
    }


    private void forceCloseConnection(Connection conn){
        if (conn != null) {
            try {
                conn.rollback();
                conn.close();
            } catch (Exception e) {
            }
        }
    }

    protected synchronized Connection getConnection(User user) throws SQLException {
        if(LOG.isDebugEnabled())
            LOG.debug("Getting connection from pool...");
        Connection conn = this.cpool.getConnection();
        if(conn.isClosed()) {
            forceCloseConnection(conn);
            if(LOG.isDebugEnabled())LOG.error("Connection is closed!");
            throw new SQLException("Connection is closed!");
        }
        if(!conn.isValid(5)) {
            forceCloseConnection(conn);
            if(LOG.isDebugEnabled())LOG.error("Connection is not valid!");
            throw new SQLException("Connection is not valid!");
        }

        if(conn != null) {
            try {
                doAfterConnect(SQLConnectionConnectedEvent.Attributes.build(conn, user));
                if(LOG.isDebugEnabled())
                    LOG.debug("Connection is ok...");
                return conn;
            } catch (Exception e) {
                forceCloseConnection(conn);
                if(LOG.isDebugEnabled())LOG.error(String.format("Unexpected error on execute doAfterConnect event! Message: %s", e.getMessage()), e);
                throw e;
            }
        }
        return null;
    }


    /**
     * Выполняет action внутри batch.
     * Перед началом выполнения создается соединение.
     * Если в процессе выполнения последовательности не было ошибок,
     * по завершении выполняется commit,
     * иначе транзакция откатывается.
     */
    @Override
    public <P, R> R execBatch (final SQLActionScalar1<P, R> batch, final P param, final User usr) {
        try {
            R result = null;
            try (Connection conn = this.getConnection(usr)) {
                conn.setAutoCommit(false);
                setCurrentContext(usr, conn);
                try {
                    if (batch != null)
                        result = batch.exec(this, param);
                    getCurrentConnection().commit();
                } catch (SQLException e) {
                    if (getCurrentConnection() != null)
                        try {
                            getCurrentConnection().rollback();
                        } catch (SQLException e1) {}
                    throw e;
                } finally {
                    closeCurrentContext();
                }
            }
            return result;
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void execBatch (final SQLActionVoid0 batch, final User usr) {
        execBatch((context, param) -> {
            if (batch != null)
                batch.exec(context);
            return null;
        }, null, usr);
    }

    @Override
    public <P> void execBatch (final SQLActionVoid1 batch, final P param, final User usr) {
        execBatch((context, prm) -> {
            if (batch != null)
                batch.exec(context, prm);
            return null;
        }, param, usr);
    }

    @Override
    public <R> R execBatch (final SQLActionScalar0<R> batch, final User usr) {
        return execBatch((context, p) -> {
            if (batch != null)
                return batch.exec(context);
            return null;
        }, null, usr);
    }

    @Override
    public SQLCursor createCursor(){
        return new DbCursor();
    }

    @Override
    public SQLCursor createDynamicCursor(){
        return new DbDynamicCursor();
    }

    @Override
    public SQLStoredProc createStoredProc(){
        DbStoredProc cmd = new DbStoredProc();
        cmd.setParamSetter(new DbCallableParamSetter());
        cmd.setParamGetter(new DbCallableParamGetter());
        return cmd;
    }

    @Override
    public abstract String getDBMSName();

    @Override
    public Wrappers getWrappers() {
        return wrappers;
    }

    @Override
    public SQLConnectionPoolConfig getConfig() {
        return config;
    }

    @Override
    public StoredProgMetadata prepareStoredProc(String sql, Connection conn, List<Param> paramsDeclaration) {
        return DbUtils.getInstance().detectStoredProcParamsAuto(sql, conn, paramsDeclaration);
    }


}
