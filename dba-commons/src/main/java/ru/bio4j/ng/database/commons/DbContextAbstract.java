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

//    private void setCurrentUser(User user) {
//        DbContextThreadHolder.setCurrentUser(user);
//    }

    public Connection getCurrentConnection() {
        return ThreadContextHolder.instance().getCurrentConnection();
    }

//    private void setCurrentConnection(Connection conn) {
//        DbContextThreadHolder.setCurrentConnection(conn);
//    }

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
        if(this.innerAfterEvents.size() > 0) {
            for(SQLConnectionConnectedEvent e : this.innerAfterEvents)
                e.handle(this, attrs);
        }
        if(this.afterEvents.size() > 0) {
            for(SQLConnectionConnectedEvent e : this.afterEvents)
                e.handle(this, attrs);
        }
    }

    protected static final int CONNECTION_TRY_COUNT = 3;
    protected static final long CONNECTION_AFTER_FAIL_PAUSE = 15L; // secs
//    protected Connection getConnection(String userName, String password) throws SQLException {
    protected Connection getConnection(User user) throws SQLException {
        LOG.debug("Getting connection from pool...");
        Connection conn = null;
        int connectionPass = 0;
        while(connectionPass < CONNECTION_TRY_COUNT) {
            connectionPass++;
            conn = this.cpool.getConnection();
            //conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
            if (conn.isClosed() || !conn.isValid(5)) {
                LOG.debug("Connection is not valid or closed...");
                conn = null;
                ((org.apache.tomcat.jdbc.pool.DataSource) this.cpool).close(true);
                LOG.debug("Waiting {} secs before next try connect to database...", CONNECTION_AFTER_FAIL_PAUSE);
                try {
                    Thread.sleep(CONNECTION_AFTER_FAIL_PAUSE * 1000);
                } catch (InterruptedException e) {}
            } else {
                LOG.debug("Connection is ok...");
                break;
            }
        }

        if(conn == null)
            LOG.debug("All trying of connecting to database ({}) failed...", CONNECTION_TRY_COUNT);

        this.doAfterConnect(SQLConnectionConnectedEvent.Attributes.build(conn, user));
        return conn;
    }


    /**
     * Выполняет action внутри batch.
     * Перед началом выполнения создается соединение.
     * Если в процессе выполнения последовательности не было ошибок,
     * по завершении выполняется commit,
     * иначе транзакция откатывается.
     */
    @Override
    public <P, R> R execBatch (final SQLActionScalar1<P, R> batch, final P param, final User usr) throws Exception {
        R result = null;
        try(Connection conn = this.getConnection(usr)){
            conn.setAutoCommit(false);
            setCurrentContext(usr, conn);
            try {
                if (batch != null)
                    result = batch.exec(this, param);
                getCurrentConnection().commit();
            } catch (Exception e) {
                if (getCurrentConnection() != null)
                    try {
                        getCurrentConnection().rollback();
                    } catch (Exception e1) {}
                throw e;
            } finally {
                closeCurrentContext();
            }
        }
        return result;
    }

    @Override
    public void execBatch (final SQLActionVoid0 batch, final User usr) throws Exception {
        execBatch((context, param) -> {
            if (batch != null)
                batch.exec(context);
            return null;
        }, null, usr);
    }

    @Override
    public <P> void execBatch (final SQLActionVoid1 batch, final P param, final User usr) throws Exception {
        execBatch((context, prm) -> {
            if (batch != null)
                batch.exec(context, prm);
            return null;
        }, param, usr);
    }

    @Override
    public <R> R execBatch (final SQLActionScalar0<R> batch, final User usr) throws Exception {
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
    public StoredProgMetadata prepareStoredProc(String sql, Connection conn, List<Param> paramsDeclaration) throws Exception {
        return DbUtils.getInstance().detectStoredProcParamsAuto(sql, conn, paramsDeclaration);
    }


}
