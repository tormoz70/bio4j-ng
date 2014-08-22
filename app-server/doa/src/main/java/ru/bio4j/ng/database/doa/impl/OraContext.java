package ru.bio4j.ng.database.doa.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.oracore.OracleType;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
//import oracle.ucp.jdbc.PoolDataSource;
//import oracle.ucp.jdbc.PoolDataSourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class OraContext implements SQLContext {
    private static final Logger LOG = LoggerFactory.getLogger(OraContext.class);

    private DataSource cpool;

    private final List<SQLConnectionConnectedEvent> afterEvents = new ArrayList<>();
    private final List<SQLConnectionConnectedEvent> innerAfterEvents = new ArrayList<>();

    private final SQLConnectionPoolConfig config;

    private OraContext(DataSource cpool, SQLConnectionPoolConfig config) {
        this.cpool = cpool;
        this.config = config;
        if(this.config.getCurrentSchema() != null) {
            this.innerAfterEvents.add(
                    new SQLConnectionConnectedEvent() {
                        @Override
                        public void handle(SQLContext sender, SQLConnectionConnectedEventAttrs attrs) throws SQLException {
                            if(attrs.getConnection() != null) {
                                String curSchema = OraContext.this.config.getCurrentSchema().toUpperCase();
                                LOG.debug("onAfterGetConnection - start setting current_schema="+curSchema);
                                CallableStatement cs1 = attrs.getConnection().prepareCall( "alter session set current_schema="+curSchema);
                                cs1.execute();
                                LOG.debug("onAfterGetConnection - OK. current_schema now is "+curSchema);
                            }
                        }
                    }
            );
        }
    }

    @Override
    public void addAfterEvent(SQLConnectionConnectedEvent e) {
        this.afterEvents.add(e);
    }

    @Override
    public void clearAfterEvents() {
        this.afterEvents.clear();
    }

    private void doAfterConnect(SQLConnectionConnectedEventAttrs attrs) throws SQLException {
        if(this.innerAfterEvents.size() > 0) {
            for(SQLConnectionConnectedEvent e : this.innerAfterEvents)
                e.handle(this, attrs);
        }
        if(this.afterEvents.size() > 0) {
            for(SQLConnectionConnectedEvent e : this.afterEvents)
                e.handle(this, attrs);
        }
    }

    public static SQLContext create(SQLConnectionPoolConfig config) throws SQLException {
        LOG.debug("Creating SQLContext with:\n" + Utl.buildBeanStateInfo(config, null, "\t"));
        final PoolProperties properties = new PoolProperties();
        properties.setMaxActive(config.getMaxPoolSize());
        properties.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        properties.setUrl(config.getDbConnectionUrl());
        properties.setUsername(config.getDbConnectionUsr());
        properties.setPassword(config.getDbConnectionPwd());
        properties.setCommitOnReturn(false);
        DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(properties);
        return new OraContext(dataSource, config);
    }

    private static final int CONNECTION_TRY_COUNT = 3;
    private static final long CONNECTION_AFTER_FAIL_PAUSE = 15L;
	public Connection getConnection(String userName, String password) throws SQLException {
        LOG.debug("Getting connection from pool...");
        Connection conn = null;
        int connectionPass = 0;
        while(connectionPass < CONNECTION_TRY_COUNT) {
            connectionPass++;
            if (Strings.isNullOrEmpty(userName))
                conn = this.cpool.getConnection();
            else
                conn = this.cpool.getConnection(userName, password);
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

        this.doAfterConnect(SQLConnectionConnectedEventAttrs.build(conn));
        return conn;
	}

    public Connection getConnection() throws SQLException {
        return getConnection(null, null);
    }

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
     * @return - объект типа T
     * @throws Exception
     */
    public <S, C, R> R execBatch (final S scope, final SQLActionExt<S, C, R> batch, final C context) throws Exception {
        R result = null;
        try(Connection conn = this.getConnection()){
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

    public <C, R> R execBatch (final SQLAction<C, R> batch, final C context) throws Exception {
        return execBatch(this, new SQLActionExt<OraContext, C, R>() {
            @Override
            public R exec(OraContext context, Connection conn, C param) throws Exception {
                if (batch != null)
                    return batch.exec(context, conn, param);
                return null;
            }
        }, context);
    }

    public <R> R execBatch (final SQLActionScalar<R> batch) throws Exception {
        return execBatch(new SQLAction<Object, R>() {
            @Override
            public R exec(SQLContext context, Connection conn, Object param) throws Exception {
                if (batch != null)
                    return batch.exec(context, conn);
                return null;
            }
        }, null);
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
        return execSQLAtomic(this, conn, new SQLActionExt<OraContext, C, R>() {
            @Override
            public R exec(OraContext context, Connection conn, C param) throws Exception {
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

//    @Override
//    public SQLConnectionPoolStat getStat(){
//        return new SQLConnectionPoolStat(
//            this.cpool.getConnectionPoolName(), this.cpool.getMinPoolSize(), this.cpool.getMaxPoolSize(), this.cpool.getConnectionWaitTimeout(), this.cpool.getInitialPoolSize(),
//            this.cpool.getStatistics().getTotalConnectionsCount(), this.cpool.getStatistics().getAvailableConnectionsCount(), this.cpool.getStatistics().getBorrowedConnectionsCount(),
//            this.cpool.getStatistics().getAverageBorrowedConnectionsCount(), this.cpool.getStatistics().getPeakConnectionsCount(), this.cpool.getStatistics().getRemainingPoolCapacityCount(),
//            this.cpool.getStatistics().getLabeledConnectionsCount(), this.cpool.getStatistics().getConnectionsCreatedCount(), this.cpool.getStatistics().getConnectionsClosedCount(),
//            this.cpool.getStatistics().getAverageConnectionWaitTime(), this.cpool.getStatistics().getPeakConnectionWaitTime(), this.cpool.getStatistics().getAbandonedConnectionsCount(),
//            this.cpool.getStatistics().getPendingRequestsCount(), this.cpool.getStatistics().getCumulativeConnectionWaitTime(), this.cpool.getStatistics().getCumulativeConnectionBorrowedCount(),
//            this.cpool.getStatistics().getCumulativeConnectionUseTime(), this.cpool.getStatistics().getCumulativeConnectionReturnedCount(), this.cpool.getStatistics().getCumulativeSuccessfulConnectionWaitTime(),
//            this.cpool.getStatistics().getCumulativeSuccessfulConnectionWaitCount(), this.cpool.getStatistics().getCumulativeFailedConnectionWaitTime(),
//            this.cpool.getStatistics().getCumulativeFailedConnectionWaitCount()
//        );
//        return null;
//    }

    @Override
    public SQLCursor CreateCursor(){
        OraCursor cmd = new OraCursor();
        cmd.setParamSetter(new OraSelectableParamSetter(cmd));
        return cmd;
    }

    @Override
    public SQLStoredProc CreateStoredProc(){
        OraStoredProc cmd = new OraStoredProc();
        cmd.setParamSetter(new OraCallableParamSetter(cmd));
        cmd.setParamGetter(new OraCallableParamGetter(cmd));
        return cmd;
    }

    @Override
    public SQLReader CreateReader(ResultSet resultSet) {
        return new OraReader(resultSet);
    }

    @Override
    public String getDBMSName() {
        return "oracle";
    }

}
