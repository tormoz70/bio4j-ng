package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.sql.*;
import java.util.List;

public interface SQLContext {
    //Connection getConnection() throws SQLException;
    //Connection getConnection(String userName, String password) throws SQLException;

    void execBatch (final SQLActionVoid batch, final User usr) throws Exception;
    <R> R execBatch (final SQLActionScalar<R> batch, final User usr) throws Exception;
    <P, R> R execBatch (final SQLAction<P, R> batch, final P param, final User usr) throws Exception;

    <P, R> R execSQLAtomic(final Connection conn, final SQLAction<P, R> batch, final P param, final User usr) throws Exception;
    <R> R execSQLAtomic(final Connection conn, final SQLActionScalar<R> batch, final User usr) throws Exception;
    void execSQLAtomic(final Connection conn, final SQLActionVoid action, final User usr) throws Exception;

    <P, R> R execSQL(final Connection conn, final SQLAction<P, R> batch, final P param, final User usr) throws Exception;
    <R> R execSQL(final Connection conn, final SQLActionScalar<R> batch, final User usr) throws Exception;
    void execSQL(final Connection conn, final SQLActionVoid action, final User usr) throws Exception;
    List<Param> execSQL(final Connection conn, final String sql, List<Param> params, final User usr) throws Exception;
    List<Param> execSQL(final String sql, List<Param> params, final User usr) throws Exception;

    StoredProgMetadata prepareStoredProc(String sql, Connection conn, List<Param> paramsDeclaration) throws Exception;

    void addAfterEvent(SQLConnectionConnectedEvent e);
    void clearAfterEvents();

    SQLCursor createCursor();
    SQLStoredProc createStoredProc();

    String getDBMSName();

    SQLConnectionPoolConfig getConfig();
    Wrappers getWrappers();
}
