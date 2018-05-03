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

//    <R> R execSQL(final Connection conn, final UpdelexSQLDef sqlDef, final List<Param> params, final User usr) throws Exception;
//    <R> R execSQL(final UpdelexSQLDef sqlDef, final List<Param> params, final User usr) throws Exception;

//    SQLConnectionPoolStat getStat();
    void addAfterEvent(SQLConnectionConnectedEvent e);
    void clearAfterEvents();

    SQLCursor createCursor();
    SQLStoredProc createStoredProc();
    //SQLReader createReader(ResultSet resultSet);

    String getDBMSName();

    SQLConnectionPoolConfig getConfig();
    Wrappers getWrappers();
}
