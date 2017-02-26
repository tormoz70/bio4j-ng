package ru.bio4j.ng.database.commons;

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
public abstract class DbCommand<T extends SQLCommand> implements SQLCommand {
    private static final Logger LOG = LoggerFactory.getLogger(DbCommand.class);

    protected final SQLContext context;
	protected List<Param> params = null;
    protected int timeout = 60;
    protected Connection connection = null;
    protected SQLNamedParametersStatement preparedStatement = null;
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

    public DbCommand(SQLContext context) {
        this.context = context;
	}

    /**
     * Присваивает значения входящим параметрам
     */
    protected void setParamsToStatement() throws Exception {
        if(this.paramSetter != null)
            this.paramSetter.setParamsToStatement(this, this.params);
    }

    protected void getBackOutParams() throws SQLException {
        if(this.paramGetter != null)
            this.paramGetter.getParamsFromStatement(this, this.params);
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
        sb.append("{DbCommand.Params(before exec): {\n");
        for (Param p : params)
            sb.append("\t"+p.toString()+",\n");
        sb.append("}}");
        return String.format("preparedSQL: %s;\n - %s", sql, sb.toString());
    }

    private static String getSQL2Execute(String sql, String params) {
        StringBuilder sb = new StringBuilder();
        sb.append("{DbCommand.Params(before exec): {\n");
        sb.append(params);
        sb.append("}}");
        return String.format("preparedSQL: %s;\n - %s", sql, sb.toString());
    }

    protected T processStatement(List<Param> params, DelegateSQLAction action) throws Exception {
        SQLException lastError = null;
        try {
            try {
                this.resetCommand(); // Сбрасываем состояние

                // Объединяем параметры
                if(this.params == null)
                    this.params = new ArrayList<>();
                if(params != null)
                    this.params = Paramus.set(params).merge(params, true).pop();

                if(!this.doBeforeStatement(this.params)) // Обрабатываем события
                    return (T)this;

                this.setParamsToStatement(); // Применяем параметры

                if (action != null) {
                    LOG.debug("Try to execute: {}", getSQL2Execute(this.preparedSQL, this.preparedStatement.getParamsAsString()));
                    action.execute(); // Выполняем команду
                }

                this.getBackOutParams(); // Вытаскиваем OUT-параметры

            } catch (SQLException e) {
                lastError = new SQLExceptionExt(String.format("%s:\n - %s;\n - %s", "Error on execute command.", getSQL2Execute(this.preparedSQL, this.preparedStatement.getParamsAsString()), e.getMessage()), e);
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
        final SQLNamedParametersStatement stmnt = this.getStatement();
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
	public SQLNamedParametersStatement getStatement() {
		return this.preparedStatement;
	}

    @Override
    public String getPreparedSQL() {
        return this.preparedSQL;
    }

    public <T> T getParamValue(String paramName, Class<T> type, T defaultValue) throws Exception {
        return Paramus.paramValue(this.getParams(), paramName, type, defaultValue);
    }

}
