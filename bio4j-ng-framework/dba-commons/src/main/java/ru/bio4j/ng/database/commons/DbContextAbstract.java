package ru.bio4j.ng.database.commons;

import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//    public Connection getConnection() throws SQLException {
//        return getConnection(null, null);
//    }

    /**
     * Выполняет batch в рамках одной транзакции.
     * Если в процессе выполнения последовательности не было ошибок,
     * по завершении выполняется commit и соединение закрывается,
     * иначе транзакция откатывается, соединение закрывается.
     * @param <S> - тип объекта, который передается в param
     * @param <C> - тип объекта, который передается в param
     * @param <R> - тип возвращаемого параметра, если есть
     * @param scope - ссылка на объект, который будет param'ом
     * @param batch - то что нужно выполнить в рамках сессии
     * @param context - дополнительные параметры, которые передаются в batch
     * @param user - профиль текущего пользователя
     * @return - объект типа T
     * @throws Exception
     */
    public <S, C, R> R execBatch (final S scope, final SQLActionExt<S, C, R> batch, final C context, final User user) throws Exception {
        R result = null;
        try(Connection conn = this.getConnection(user)){
            conn.setAutoCommit(false);
            try {
                if (batch != null)
                    result = batch.exec(scope, conn, context);
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

    public <C, R> R execBatch (final SQLAction<C, R> batch, final C context, final User user) throws Exception {
        return execBatch(this, new SQLActionExt<DbContextAbstract, C, R>() {
            @Override
            public R exec(DbContextAbstract context, Connection conn, C param) throws Exception {
                if (batch != null)
                    return batch.exec(context, conn, param);
                return null;
            }
        }, context, user);
    }

    public <R> R execBatch (final SQLActionScalar<R> batch, final User user) throws Exception {
        return execBatch(new SQLAction<Object, R>() {
            @Override
            public R exec(SQLContext context, Connection conn, Object param) throws Exception {
                if (batch != null)
                    return batch.exec(context, conn);
                return null;
            }
        }, null, user);
    }

    /**
     * Выполняет action внутри batch.
     * Перед началом выполнения создается "точка отката".
     * Если в процессе выполнения последовательности не было ошибок,
     * по завершении выполняется удаление "точки отката" и возврат в родительский batch,
     * иначе транзакция откатывается в "точку отката" и ошибка передается в родительский процесс.
     * Если необходимо просто откатить транзакцию в "точку отката", то внутри action надо возбудить RollbackSQLAtomic.
     * При этом транзакция откатится в "точку отката" и управление вернется в ротительскую процедуру без возбуждения какой либо ошибки.
     * @param <S> - тип объекта, который передается в param
     * @param <C> - тип объекта, который передается в param
     * @param <R> - тип возвращаемого параметра, если есть
     * @param scope - ссылка на объект, который будет param'ом
     * @param conn - сессия в рамкох которой необходимо выполнить атомарную операцию
     * @param action - то, что нужно выполнить
     * @param context - дополнительные параметры, которые передаются в action
     * @return
     * @throws Exception
     */
    public <S, C, R> R execSQLAtomic(final S scope, final Connection conn, final SQLActionExt<S, C, R> action, final C context) throws Exception {
        final String savePointId = "DbAtomicActionSavePoint"; //Guid.NewGuid().ToString();
        Savepoint savepoint = conn.setSavepoint(savePointId);
        try {
            if (action != null){
                return action.exec(scope, conn, context);
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

    public <C, R> R execSQLAtomic(final Connection conn, final SQLAction<C, R> action, final C context) throws Exception {
        return execSQLAtomic(this, conn, new SQLActionExt<DbContextAbstract, C, R>() {
            @Override
            public R exec(DbContextAbstract context, Connection conn, C param) throws Exception {
                if(action != null)
                    action.exec(context, conn, param);
                return null;
            }
        }, context);
    }

    public <R> R execSQLAtomic(final Connection conn, final SQLActionScalar<R> action) throws Exception {
        return execSQLAtomic(conn, new SQLAction<Object, R>() {
            @Override
            public R exec(SQLContext context, Connection conn, Object param) throws Exception {
                if(action != null)
                    action.exec(context, conn);
                return null;
            }
        }, null);
    }

    @Override
    public SQLCursor createCursor(){
        return new DbCursor(this);
    }

    @Override
    public SQLStoredProc createStoredProc(){
        DbStoredProc cmd = new DbStoredProc(this);
        cmd.setParamSetter(new DbCallableParamSetter(cmd));
        cmd.setParamGetter(new DbCallableParamGetter(cmd));
        return cmd;
    }

    @Override
    public SQLReader createReader(ResultSet resultSet) {
        return new DbReader(resultSet);
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
