package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.commons.utils.SrvcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class DbCursor extends DbCommand<SQLCursor> implements SQLCursor {
    private static final Logger LOG = LoggerFactory.getLogger(DbCursor.class);


	private boolean isActive = false;

	private String sql = null;
    private SQLReader reader;

    public DbCursor() {
        this.setParamSetter(new DbSelectableParamSetter());
        this.reader = createReader();
    }

	@Override
	public SQLCursor init(Connection conn, String sql, List<Param> params, int timeout) throws Exception {
        if(Strings.isNullOrEmpty(sql))
            throw new IllegalArgumentException("Parameter \"sql\" cannon be empty!!!");
        this.sql = sql;
		return super.init(conn, params, timeout);
	}

    @Override
    public SQLCursor init(Connection conn, String sql, List<Param> params) throws Exception {
        return this.init(conn, sql, params, 60);
    }

    @Override
    public SQLCursor init(Connection conn, String sql) throws Exception {
        return this.init(conn, sql, null, 60);
    }


    @Override
	protected void prepareStatement() throws SQLException {
        this.preparedSQL = this.sql;
        this.preparedStatement = DbNamedParametersStatement.prepareStatement(this.connection, this.preparedSQL);
        this.preparedStatement.setQueryTimeout(this.timeout);
	}

    @Override
    public SQLReader createReader() {
        return new DbReader();
    }

    @Override
	public String getSQL() {
		return this.sql;
	}

    @Override
    public boolean fetch(List<Param> params, User usr, DelegateSQLFetch onrecord) throws Exception {
        boolean rslt = false;
        Exception lastError = null;
        try {
            List<Param> prms = params != null ? params : new ArrayList<>();
            SrvcUtils.applyCurrentUserParams(usr, prms);
            try {
                this.resetCommand(); // Сбрасываем состояние

                if (this.params == null)
                    this.params = new ArrayList<>();

                applyInParamsToStatmentParams(params, false);

                if (!doBeforeStatement(this.params)) // Обрабатываем события
                    return rslt;

                setParamsToStatement(); // Применяем параметры

                LOG.debug("Try to execute: {}", getSQL2Execute(this.preparedSQL, this.preparedStatement.getParamsAsString()));
                try (ResultSet result = this.preparedStatement.executeQuery()) {
                    this.isActive = true;
                    while (this.reader.next(result)) {
                        rslt = true;
                        if (onrecord != null) {
                            if (!onrecord.callback(this.reader))
                                break;
                        }
                    }
                }

            //} catch (SQLException e) {
            //    lastError = new SQLExceptionExt(String.format("%s:\n - %s;\n - %s", "Error on execute command.", getSQL2Execute(this.preparedSQL, this.preparedStatement.getParamsAsString()), e.getMessage()), e);
            //    throw lastError;
            } catch (Exception e) {
                lastError = new Exception(String.format("%s:\n - %s;\n - %s", "Error on execute command.", getSQL2Execute(this.preparedSQL, this.params), e.getMessage()), e);
            }
        } finally {
            if (this.preparedStatement != null)
                try {
                    this.preparedStatement.close();
                } catch (Exception ignore) {
                }
            if(lastError != null)
                throw lastError;
            return rslt;
        }
    }

    @Override
    public boolean fetch(User usr, DelegateSQLFetch onrecord) throws Exception {
        return this.fetch(null, usr, onrecord);
    }

    private static class ScalarResult<T> {
        public T result;
    }

    @Override
    public <T> T scalar(final List<Param> params, final User usr, final String fieldName, final Class<T> clazz, T defaultValue) throws Exception {
        final ScalarResult<T> rslt = new ScalarResult();
        if(this.fetch(params, usr, (rs -> {
            if(rs.getFields().size() > 0) {
                if(Strings.isNullOrEmpty(fieldName))
                    rslt.result = rs.getValue(1, clazz);
                else
                    rslt.result = rs.getValue(fieldName, clazz);
            }
            return false;
        })))
            return Converter.toType(rslt.result, clazz);
        return defaultValue;
    }

    @Override
    public <T> T scalar(final List<Param> params, final User usr, final Class<T> clazz, T defaultValue) throws Exception {
        return scalar(params, usr, null, clazz, defaultValue);
    }

    @Override
    public <T> T scalar(final User usr, final String fieldName, final Class<T> clazz, T defaultValue) throws Exception {
        return scalar(null, usr, fieldName, clazz, defaultValue);
    }

    @Override
    public <T> T scalar(final User usr, final Class<T> clazz, T defaultValue) throws Exception {
        return scalar(null, usr, null, clazz, defaultValue);
    }

    @Override
    public <T> List<T> beans(final List<Param> params, final User usr, final Class<T> clazz) throws Exception {
        final List<T> rslt = new ArrayList<>();
        this.fetch(null, usr, (rs -> {
            rslt.add(DbUtils.createBeanFromReader(rs, clazz));
            return true;
        }));
        return rslt;
    }

    @Override
    public <T> List<T> beans(final User usr, final Class<T> clazz) throws Exception {
        return beans(null, usr, clazz);
    }

    @Override
    public <T> T firstBean(final List<Param> params, final User usr, final Class<T> clazz) throws Exception {
        final List<T> rslt = new ArrayList<>();
        this.fetch(params, usr, (rs -> {
            rslt.add(DbUtils.createBeanFromReader(rs, clazz));
            return false;
        }));
        return rslt.size() > 0 ? rslt.get(0) : null;
    }

    @Override
    public <T> T firstBean(final User usr, final Class<T> clazz) throws Exception {
        return firstBean(null, usr, clazz);
    }

    @Override
    protected void applyInParamsToStatmentParams(List<Param> params, boolean overwriteType) throws Exception {
        DbUtils.applyParamsToParams(params, this.params, false, true, overwriteType);
    }

}
