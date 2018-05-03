package ru.bio4j.ng.database.commons;

import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.UpdelexSQLDef;

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
//            if (Strings.isNullOrEmpty(userName))
            conn = this.cpool.getConnection();
//            else
//                conn = this.cpool.getConnection(userName, password);
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
    public <P, R> R execBatch (final SQLAction<P, R> batch, final P param, final User usr) throws Exception {
        R result = null;
        try(Connection conn = this.getConnection(usr)){
            conn.setAutoCommit(false);
            try {
                if (batch != null)
                    result = batch.exec(this, conn, param, usr);
                conn.commit();
            } catch (Exception e) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e1) {}
                throw e;
            }
        }
        return result;
    }

    @Override
    public void execBatch (final SQLActionVoid batch, final User usr) throws Exception {
        execBatch((context, conn, param, u) -> {
            if (batch != null)
                batch.exec(context, conn, u);
            return null;
        }, null, usr);
    }

    @Override
    public <R> R execBatch (final SQLActionScalar<R> batch, final User usr) throws Exception {
        return execBatch((context, conn, param, u) -> {
            if (batch != null)
                return batch.exec(context, conn, u);
            return null;
        }, null, usr);
    }

    /**
     * Выполняет execSQL - курсор.
     */
//    @Override
//    public <R> R execSQL (final Connection connection, final UpdelexSQLDef sqlDef, final List<Param> params, final User usr) throws Exception {
//        SQLStoredProc sp = this.createStoredProc();
//        sp.init(connection, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration()).execSQL(params, usr);
//        List<Param> outparams = sqlDef.getParamDeclaration();
//        for (Param p : outparams)
//            if (p.getDirection() == Param.Direction.INOUT || p.getDirection() == Param.Direction.OUT)
//                return (R) p.getValue();
//        return null;
//    }

//    @Override
//    public <R> R execSQL (final BioCursorDeclaration.UpdelexSQLDef sqlDef, final List<Param> params, final User usr) throws Exception {
//        return execBatch((context, conn, param, u) -> execSQL(conn, sqlDef, params, u), null, usr);
//    }

    /**
     * Выполняет action внутри соединения.
     */
    @Override
    public <P, R> R execSQL(final Connection conn, final SQLAction<P, R> batch, final P param, final User usr) throws Exception {
        if (batch != null)
            return batch.exec(this, conn, param, usr);
        return null;
    }


    @Override
    public <R> R execSQL(final Connection conn, final SQLActionScalar<R> batch, final User usr) throws Exception {
        if (batch != null)
            return batch.exec(this, conn, usr);
        return null;
    }

    @Override
    public void execSQL(final Connection conn, final SQLActionVoid action, final User usr) throws Exception {
            if (action != null)
                action.exec(this, conn, usr);
    }

    /**
     * Выполняет action внутри batch.
     * Перед началом выполнения создается "точка отката".
     * Если в процессе выполнения последовательности не было ошибок,
     * по завершении выполняется удаление "точки отката" и возврат в родительский batch,
     * иначе транзакция откатывается в "точку отката" и ошибка передается в родительский процесс.
     * Если необходимо просто откатить транзакцию в "точку отката", то внутри action надо возбудить RollbackSQLAtomic.
     * При этом транзакция откатится в "точку отката" и управление вернется в ротительскую процедуру без возбуждения какой либо ошибки.
     */
    @Override
    public <P, R> R execSQLAtomic(final Connection conn, final SQLAction<P, R> batch, final P param, final User usr) throws Exception {
        R result = null;
        final String savePointId = "DbAtomicActionSavePoint"; //Guid.NewGuid().ToString();
        Savepoint savepoint = conn.setSavepoint(savePointId);
        try {
            if (batch != null){
                return batch.exec(this, conn, param, usr);
            }
            conn.releaseSavepoint(savepoint);
        } catch (RollbackSQLAtomic e) {
            conn.rollback(savepoint);
        } catch (Exception e) {
            conn.rollback(savepoint);
            throw e;
        }
        return null;
    }


    @Override
    public <R> R execSQLAtomic(final Connection conn, final SQLActionScalar<R> batch, final User usr) throws Exception {
        return execSQLAtomic(conn, (context, conn1, param)->{
            if (batch != null)
                return batch.exec(context, conn1, usr);
            return null;
        }, null);
    }

    @Override
    public void execSQLAtomic(final Connection conn, final SQLActionVoid action, final User usr) throws Exception {
        execSQLAtomic(conn, (context, conn1, param) -> {
            if(action != null)
                action.exec(context, conn1, usr);
            return null;
        }, null);
    }


    @Override
    public SQLCursor createCursor(){
        return new DbCursor();
    }

    @Override
    public SQLStoredProc createStoredProc(){
        DbStoredProc cmd = new DbStoredProc();
        cmd.setParamSetter(new DbCallableParamSetter(cmd));
        cmd.setParamGetter(new DbCallableParamGetter(cmd));
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

    public static <T extends DbContextAbstract> SQLContext create(SQLConnectionPoolConfig config, Class<T> clazz) throws Exception {
        LOG.debug("Creating SQLContext with:\n" + Utl.buildBeanStateInfo(config, null, "\t"));
        final PoolProperties properties = new PoolProperties();
        properties.setMaxActive(config.getMaxPoolSize());
        properties.setDriverClassName(config.getDbDriverName());
        properties.setUrl(config.getDbConnectionUrl());
        properties.setUsername(config.getDbConnectionUsr());
        properties.setPassword(config.getDbConnectionPwd());
        properties.setCommitOnReturn(false);
        DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(properties);
        Constructor<T> constructor = clazz.getConstructor(DataSource.class, SQLConnectionPoolConfig.class);
        return constructor.newInstance(new Object[]{dataSource, config});
    }

}
