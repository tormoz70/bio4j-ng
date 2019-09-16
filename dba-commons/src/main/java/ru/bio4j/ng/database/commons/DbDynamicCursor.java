package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.utils.SrvcUtils;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.DelegateSQLFetch;
import ru.bio4j.ng.database.api.SQLCursor;
import ru.bio4j.ng.database.api.SQLReader;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализует 3 основных вида запроса Query, Exec, Scalar
 */
public class DbDynamicCursor extends DbCursor implements SQLCursor {
    private static final Logger LOG = LoggerFactory.getLogger(DbDynamicCursor.class);

    @Override
	protected void prepareStatement() {
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
    public boolean fetch(List<Param> params, User usr, DelegateSQLFetch onrecord) {
        boolean rslt = false;
//        Exception lastError = null;
        List<Param> prms = params != null ? params : new ArrayList<>();
        SrvcUtils.applyCurrentUserParams(usr, prms);
//            try {
        this.resetCommand(); // Сбрасываем состояние

        if (this.params == null)
            this.params = new ArrayList<>();

        applyInParamsToStatmentParams(params, false);

        if (!doBeforeStatement(this.params)) // Обрабатываем события
            return rslt;

        this.preparedSQL = this.sql;
        // Удаляем из SQL условия #cut#
        this.preparedSQL = DbUtils.cutFilterConditions(this.sql, this.params);
        this.preparedStatement = DbNamedParametersStatement.prepareStatement(this.connection, this.preparedSQL);
        preparedStatement.setQueryTimeout(this.timeout);

        setParamsToStatement(); // Применяем параметры
        try {

            if (LOG.isDebugEnabled())
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
        } catch (SQLException e) {
            throw new SQLExceptionExt(e);
//            } catch (Exception e) {
//                lastError = new Exception(String.format("%s:\n - %s;\n - %s", "Error on execute command.", getSQL2Execute(this.preparedSQL, this.params), e.getMessage()), e);
//            }
        } finally {
            if (this.preparedStatement != null)
                try {
                    this.preparedStatement.close();
                } catch (Exception ignore) {
                }
//            if(lastError != null)
//                throw lastError;
        }
        return rslt;
    }

}
