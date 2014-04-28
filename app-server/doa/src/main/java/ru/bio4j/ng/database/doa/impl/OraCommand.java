package ru.bio4j.ng.database.doa.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.*;
import ru.bio4j.ng.commons.types.DelegateSQLAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.Param;

/**
 * Базовый класс
 */
public abstract class OraCommand <T extends SQLCommandBase> implements SQLCommandBase {
    private static final Logger LOG = LoggerFactory.getLogger(OraCommand.class);

	public static final int ORAERRCODE_APP_ERR_START = 20000; // начало диапазона кодов ошибок приложения в Oracle
    public static final int ORAERRCODE_USER_BREAKED = 1013; // {"ORA-01013: пользователем запрошена отмена текущей операции"}
	protected List<Param> params = null;
    protected int timeout = 60;
    protected OracleConnection connection = null;
    protected OraclePreparedStatement preparedStatement = null;
//    protected OracleResultSet resultSet = null;
    protected String preparedSQL = null;

    protected SQLWrapper sqlWrapper;

    protected OraParamSetter paramSetter;
    protected OraParamGetter paramGetter;

	protected boolean closeConnectionOnFinish = false;

    protected List<SQLCommandBeforeEvent> beforeEvents = new ArrayList<>();
    protected List<SQLCommandAfterEvent> afterEvents = new ArrayList<>();

    @Override
    public void addBeforeEvent(SQLCommandBeforeEvent e) {
        this.beforeEvents.add(e);
    }

    @Override
    public void addAfterEvent(SQLCommandAfterEvent e) {
        this.afterEvents.add(e);
    }

    @Override
    public void clearBeforeEvents() {
        this.beforeEvents.clear();
    }

    @Override
    public void clearAfterEvents() {
        this.afterEvents.clear();
    }

    public OraCommand() {
	}

    /**
     * Присваивает значения входящим параметрам
     */
    protected void setParamsToStatement() throws SQLException {
        if(this.paramSetter != null)
            this.paramSetter.setParamsToStatement(this.preparedStatement, this.params);
    }

    protected void getBackOutParams() throws SQLException {
        if((this.paramGetter != null) && (this.preparedStatement instanceof OracleCallableStatement))
            this.paramGetter.getParamsFromStatement((OracleCallableStatement)this.preparedStatement, this.params);
    }

    public void setParamSetter(OraParamSetter paramSetter) {
        this.paramSetter = paramSetter;
    }

    public void setParamGetter(OraParamGetter paramGetter) {
        this.paramGetter = paramGetter;
    }

	public T init(Connection conn, List<Param> params, int timeout) throws SQLException {
		this.connection = (OracleConnection)conn;
		this.timeout = timeout;
		this.params = params;
        this.prepareStatement();
		return (T)this;
	}
    public T init(Connection conn, List<Param> params) throws SQLException {
        return this.init(conn, params, 60);
    }

	protected abstract void prepareStatement() throws SQLException;

    protected boolean doBeforeStatement(List<Param> params) throws SQLException {
        boolean locCancel = false;
        if(this.beforeEvents.size() > 0) {
            for(SQLCommandBeforeEvent e : this.beforeEvents){
                SQLCommandBeforeEventAttrs attrs = new SQLCommandBeforeEventAttrs(false, params);
                e.handle(this, attrs);
                locCancel = locCancel || attrs.getCancel();
            }
        }
        if(locCancel)
            throw new SQLException("Command has been canceled!");
        return !locCancel;
	}

    protected void doAfterStatement(SQLCommandAfterEventAttrs attrs){
        if(this.afterEvents.size() > 0) {
            for(SQLCommandAfterEvent e : this.afterEvents)
                e.handle(this, attrs);
        }
    }

    protected T processStatement(List<Param> params, DelegateSQLAction action) throws SQLException {
        SQLException lastError = null;
        try {
            try {
                this.resetCommand(); // Сбрасываем состояние

                // Объединяем параметры
                if(this.params == null)
                    this.params = new ArrayList<>();
                if(params != null)
                    this.params = Paramus.set(params).merge(params, true).get();

                if(!this.doBeforeStatement(this.params)) // Обрабатываем события
                    return (T)this;

                this.setParamsToStatement(); // Применяем параметры

                if (action != null)
                    action.execute(); // Выполняем команду

                this.getBackOutParams(); // Вытаскиваем OUT-параметры

            } catch (SQLException e) {
//                if(LOG.isDebugEnabled()){
                    StringBuilder sb = new StringBuilder();
                    sb.append("{OraCommand.Params(before exec): {\n");
                    for (Param p : this.params)
                        sb.append("\t"+p.toString()+",\n");
                    sb.append("}}");
//                    LOG.debug(sb.toString());
//                }
                lastError = new SQLExceptionExt(String.format("%s:\n - sql: %s;\n - %s", "Error on execute command", this.preparedSQL, sb.toString()), e);
                throw lastError;
            }
        } finally {

            this.doAfterStatement(SQLCommandAfterEventAttrs.build( // Обрабатываем события
                    this.params, lastError
            ));
        }
        return (T)this;
    }

    protected void resetCommand() throws SQLException {
    }



//	@SuppressWarnings("unchecked")
//    @Override
//	public <T> T execScalar(Class<T> clazz, Params params){
//        return (T)this.processStatement(params, new DelegateSQLAction<T>() {
//            @Override
//            public T execute() throws SQLException {
//                final OraCommand self = OraCommand.this;
//                try(OracleResultSet resultSet = (OracleResultSet)self.preparedStatement.executeQuery()) {
//                    if(resultSet.next())
//                        return (T)resultSet.getObject(1);
//                }
//                return null;
//            }
//        });
//	}
//
//    @Override
//    public <T> T execScalar(Class<T> clazz){
//        return this.execScalar(clazz, null);
//    }

	@Override
	public void cancel() throws SQLException {
        final Statement stmnt = this.getStatement();
        if(stmnt != null)
            stmnt.cancel();
	}

	@Override
	public List<Param> getParams() {
        if(this.params == null)
            this.params = new ArrayList<>();
		return this.params;
	}

	@Override
	public Connection getConnection() {
		return this.connection;
	}

	@Override
	public Statement getStatement() {
		return this.preparedStatement;
	}


    @Override
    public void setSqlWrapper(SQLWrapper sqlWrapper) {
        this.sqlWrapper = sqlWrapper;
    }


    @Override
    public String getPreparedSQL() {
        return this.preparedSQL;
    }

}
