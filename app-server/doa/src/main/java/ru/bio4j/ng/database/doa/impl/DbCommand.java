package ru.bio4j.ng.database.doa.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ru.bio4j.ng.commons.types.DelegateSQLAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.Param;

/**
 * Базовый класс
 */
public abstract class DbCommand<T extends SQLCommandBase> implements SQLCommandBase {
    private static final Logger LOG = LoggerFactory.getLogger(DbCommand.class);

	protected List<Param> params = null;
    protected int timeout = 60;
    protected Connection connection = null;
    protected PreparedStatement preparedStatement = null;
    protected String preparedSQL = null;

    protected SQLParamSetter paramSetter;
    protected SQLParamGetter paramGetter;

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

    public DbCommand() {
	}

    /**
     * Присваивает значения входящим параметрам
     */
    protected void setParamsToStatement() throws SQLException {
        if(this.paramSetter != null)
            this.paramSetter.setParamsToStatement(this.preparedStatement, this.params);
    }

    protected void getBackOutParams() throws SQLException {
        if(this.paramGetter != null)
            this.paramGetter.getParamsFromStatement((CallableStatement)this.preparedStatement, this.params);
    }

    public void setParamSetter(SQLParamSetter paramSetter) {
        this.paramSetter = paramSetter;
    }

    public void setParamGetter(SQLParamGetter paramGetter) {
        this.paramGetter = paramGetter;
    }

	public T init(Connection conn, List<Param> params, int timeout) throws SQLException {
		this.connection = conn;
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
                SQLCommandBeforeEvent.Attributes attrs = new SQLCommandBeforeEvent.Attributes(false, params);
                e.handle(this, attrs);
                locCancel = locCancel || attrs.getCancel();
            }
        }
        if(locCancel)
            throw new SQLException("Command has been canceled!");
        return !locCancel;
	}

    protected void doAfterStatement(SQLCommandAfterEvent.Attributes attrs){
        if(this.afterEvents.size() > 0) {
            for(SQLCommandAfterEvent e : this.afterEvents)
                e.handle(this, attrs);
        }
    }

    private static String getSQL2Execute(String sql, List<Param> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("{OraCommand.Params(before exec): {\n");
        for (Param p : params)
            sb.append("\t"+p.toString()+",\n");
        sb.append("}}");
        return String.format("preparedSQL: %s;\n - %s", sql, sb.toString());
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

                if (action != null) {
                    LOG.debug("Try to execute: {}", getSQL2Execute(this.preparedSQL, this.params));
                    action.execute(); // Выполняем команду
                }

                this.getBackOutParams(); // Вытаскиваем OUT-параметры

            } catch (SQLException e) {
                lastError = new SQLExceptionExt(String.format("%s:\n - %s;\n - %s", "Error on execute command.", getSQL2Execute(this.preparedSQL, this.params), e.getMessage()), e);
                throw lastError;
            }
        } finally {

            this.doAfterStatement(SQLCommandAfterEvent.Attributes.build( // Обрабатываем события
                    this.params, lastError
            ));
        }
        return (T)this;
    }

    protected void resetCommand() throws SQLException {
    }

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
    public String getPreparedSQL() {
        return this.preparedSQL;
    }

}
