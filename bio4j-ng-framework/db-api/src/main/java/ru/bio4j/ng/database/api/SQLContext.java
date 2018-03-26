package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.sql.*;
import java.util.List;

public interface SQLContext {
    //Connection getConnection() throws SQLException;
    //Connection getConnection(String userName, String password) throws SQLException;

    void execBatch (final SQLActionVoid batch, final User user) throws Exception;
    <R> R execBatch (final SQLActionScalar<R> batch, final User user) throws Exception;
    <P, R> R execBatch (final SQLAction<P, R> batch, P param, final User user) throws Exception;

    <P, R> R execSQLAtomic(final Connection conn, final SQLAction<P, R> batch, final P param) throws Exception;
    <R> R execSQLAtomic(final Connection conn, final SQLActionScalar<R> batch) throws Exception;
    void execSQLAtomic(final Connection conn, final SQLActionVoid action) throws Exception;

    <P, R> R execSQL(final Connection conn, final SQLAction<P, R> batch, final P param) throws Exception;
    <R> R execSQL(final Connection conn, final SQLActionScalar<R> batch) throws Exception;
    void execSQL(final Connection conn, final SQLActionVoid action) throws Exception;

    <R> R execSQL(final Connection conn, final BioCursor.UpdelexSQLDef sqlDef, final List<Param> params) throws Exception;
    <R> R execSQL(final BioCursor.UpdelexSQLDef sqlDef, final List<Param> params, final User user) throws Exception;
    <R> R execSQL(final BioCursor.UpdelexSQLDef sqlDef, final List<Param> params) throws Exception;

//    SQLConnectionPoolStat getStat();
    void addAfterEvent(SQLConnectionConnectedEvent e);
    void clearAfterEvents();

    SQLCursor createCursor();
    SQLStoredProc createStoredProc();
    SQLReader createReader(ResultSet resultSet);

    String getDBMSName();

    SQLConnectionPoolConfig getConfig();
    Wrappers getWrappers();
}
