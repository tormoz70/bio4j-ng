package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface SQLCursor extends SQLCommand, AutoCloseable {
    SQLCursor init(Connection conn, String sql, List<Param> prms, int timeout) throws Exception;
    SQLCursor init(Connection conn, String sql, List<Param> prms) throws Exception;
    SQLCursor init(Connection conn, String sql) throws Exception;
    SQLCursor init(Connection conn, BioCursorDeclaration.SelectSQLDef sqlDef, int timeout) throws Exception;
    SQLCursor init(Connection conn, BioCursorDeclaration.SelectSQLDef sqlDef) throws Exception;

    String getSQL();

    SQLReader createReader(ResultSet resultSet);

    SQLCursor open(List<Param> params, User usr) throws Exception;
    SQLCursor open(User usr) throws Exception;
    boolean isActive();

    SQLReader reader();
}
