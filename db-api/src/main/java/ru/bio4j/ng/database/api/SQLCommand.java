package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface SQLCommand {

    List<Param> getParams();

    void cancel() throws SQLException;

	Connection getConnection();

	SQLNamedParametersStatement getStatement();

    void addBeforeEvent(SQLCommandBeforeEvent e);
    void addAfterEvent(SQLCommandAfterEvent e);
    void clearBeforeEvents();
    void clearAfterEvents();

    String getPreparedSQL();

    <T> T getParamValue(String paramName, Class<T> type, T defaultValue);

}
