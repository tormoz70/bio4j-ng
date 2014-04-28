package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface SQLCommandBase {

    List<Param> getParams();

    void cancel() throws SQLException;

	Connection getConnection();

	Statement getStatement();

    void addBeforeEvent(SQLCommandBeforeEvent e);
    void addAfterEvent(SQLCommandAfterEvent e);
    void clearBeforeEvents();
    void clearAfterEvents();

    void setSqlWrapper(SQLWrapper sqlWrapper);

    String getPreparedSQL();

}
