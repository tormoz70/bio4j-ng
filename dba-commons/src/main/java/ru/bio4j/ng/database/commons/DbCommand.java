package ru.bio4j.ng.database.commons;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.database.api.SQLDef;

/**
 * Базовый класс
 */
public abstract class DbCommand<T extends SQLCommand> implements SQLCommand {
    private static final Logger LOG = LoggerFactory.getLogger(DbCommand.class);

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

    /**
     * Присваивает значения входящим параметрам
     */
    protected void setParamsToStatement() throws SQLException {
        if(this.paramSetter != null)
            this.paramSetter.setParamsToStatement(this.preparedStatement, this.params);
    }

    protected void getParamsFromStatement() throws SQLException {
        if(this.paramGetter != null)
            this.paramGetter.getParamsFromStatement(this.preparedStatement, this.params);
    }

    protected void setParamSetter(SQLParamSetter paramSetter) {
        this.paramSetter = paramSetter;
    }

    protected void setParamGetter(SQLParamGetter paramGetter) {
        this.paramGetter = paramGetter;
    }

	public T init(Connection conn, SQLDef sqlDef, int timeout) {
		this.connection = conn;
		this.timeout = timeout;
		if(sqlDef != null)
            this.params = Paramus.clone(sqlDef.getParamDeclaration());
        this.prepareStatement();
		return (T)this;
	}
    public T init(Connection conn, SQLDef sqlDef) {
        return this.init(conn, sqlDef, 60);
    }

	protected abstract void prepareStatement();

    protected boolean doBeforeStatement(List<Param> params) {
        boolean locCancel = false;
        if(this.beforeEvents.size() > 0) {
            for(SQLCommandBeforeEvent e : this.beforeEvents){
                SQLCommandBeforeEvent.Attributes attrs = new SQLCommandBeforeEvent.Attributes(false, params);
                e.handle(this, attrs);
                locCancel = locCancel || attrs.getCancel();
            }
        }
        if(locCancel)
            throw new BioSQLException("Command has been canceled!");
        return !locCancel;
	}

    protected void doAfterStatement(SQLCommandAfterEvent.Attributes attrs){
        if(this.afterEvents.size() > 0) {
            for(SQLCommandAfterEvent e : this.afterEvents)
                e.handle(this, attrs);
        }
    }

    protected static String getSQL2Execute(String sql, List<Param> params) {
        StringBuilder sb = new StringBuilder();
        if(params != null) {
            sb.append("{DbCommand.Params(before exec): ");
            sb.append(Paramus.paramsAsString(params));
            sb.append("}");
        }
        return String.format("preparedSQL: %s;\n - %s", sql, sb.toString());
    }

    protected static String getSQL2Execute(String sql, String params) {
        StringBuilder sb = new StringBuilder();
        if(!Strings.isNullOrEmpty(params)) {
            sb.append("{DbCommand.Params(before exec): {\n");
            sb.append(params);
            sb.append("}}");
        }
        return String.format("preparedSQL: %s;\n - %s", sql, sb.toString());
    }

    protected abstract void applyInParamsToStatmentParams(List<Param> params, boolean overwriteType);

    protected void resetCommand() {
    }

	@Override
	public void cancel() {
        final SQLNamedParametersStatement stmnt = this.getStatement();
        if(stmnt != null) {
            try {
                stmnt.cancel();
            } catch (Exception ignore) {}
        }
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

    public <T> T getParamValue(String paramName, Class<T> type, T defaultValue) {
        return Paramus.paramValue(this.getParams(), paramName, type, defaultValue);
    }

}
