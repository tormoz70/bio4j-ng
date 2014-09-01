package ru.bio4j.ng.database.commons;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public abstract class DbContextAbstract implements SQLContext {
    private static final Logger LOG = LoggerFactory.getLogger(DbContextAbstract.class);

    protected DataSource cpool;

    protected final List<SQLConnectionConnectedEvent> afterEvents = new ArrayList<>();
    protected final List<SQLConnectionConnectedEvent> innerAfterEvents = new ArrayList<>();

    protected final SQLConnectionPoolConfig config;

    protected DbContextAbstract(DataSource cpool, SQLConnectionPoolConfig config) throws Exception {
        this.cpool = cpool;
        this.config = config;
        if(this.config.getCurrentSchema() != null) {
            this.innerAfterEvents.add(
                    new SQLConnectionConnectedEvent() {
                        @Override
                        public void handle(SQLContext sender, Attributes attrs) throws SQLException {
                            if(attrs.getConnection() != null) {
                                String curSchema = DbContextAbstract.this.config.getCurrentSchema().toUpperCase();
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
    protected abstract Connection getConnection(String userName, String password) throws SQLException;

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
        return execBatch(this, new SQLActionExt<DbContextAbstract, C, R>() {
            @Override
            public R exec(DbContextAbstract context, Connection conn, C param) throws Exception {
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
    public SQLCursor CreateCursor(){
        return new DbCursor(this);
    }

    @Override
    public SQLStoredProc CreateStoredProc(){
        DbStoredProc cmd = new DbStoredProc(this);
        cmd.setParamSetter(new DbCallableParamSetter(cmd));
        cmd.setParamGetter(new DbCallableParamGetter(cmd));
        return cmd;
    }

    @Override
    public SQLReader CreateReader(ResultSet resultSet) {
        return new DbReader(resultSet);
    }

    @Override
    public abstract String getDBMSName();

    @Override
    public abstract Wrappers getWrappers();
}
