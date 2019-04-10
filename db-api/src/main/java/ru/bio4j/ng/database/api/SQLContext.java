package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.sql.*;
import java.util.List;

public interface SQLContext {
    //Connection getConnection() throws SQLException;
    //Connection getConnection(String userName, String password) throws SQLException;

    User getCurrentUser();
    Connection getCurrentConnection();

    void execBatch (final SQLActionVoid0 batch, final User usr) throws Exception;
    <P> void execBatch (final SQLActionVoid1 batch, final P param, final User usr) throws Exception;
    <R> R execBatch (final SQLActionScalar0<R> batch, final User usr) throws Exception;
    <P, R> R execBatch (final SQLActionScalar1<P, R> batch, final P param, final User usr) throws Exception;

//    <P, R> R execSQLAtomic(final Connection conn, final SQLAction1<P, R> batch, final P param, final User usr) throws Exception;
//    <R> R execSQLAtomic(final Connection conn, final SQLActionScalar0<R> batch, final User usr) throws Exception;
//    void execSQLAtomic(final Connection conn, final SQLActionVoid0 action, final User usr) throws Exception;
//
//    <P, R> R execSQL(final Connection conn, final SQLAction1<P, R> batch, final P param, final User usr) throws Exception;
//    <R> R execSQL(final Connection conn, final SQLActionScalar0<R> batch, final User usr) throws Exception;
//    void execSQL(final Connection conn, final SQLActionVoid0 action, final User usr) throws Exception;
//    List<Param> execSQL(final Connection conn, final String sql, List<Param> params, final User usr) throws Exception;
//    List<Param> execSQL(final String sql, List<Param> params, final User usr) throws Exception;

    StoredProgMetadata prepareStoredProc(String sql, Connection conn, List<Param> paramsDeclaration) throws Exception;

    void addAfterEvent(SQLConnectionConnectedEvent e);
    void clearAfterEvents();

    SQLCursor createCursor();
    SQLCursor createDynamicCursor();
    SQLStoredProc createStoredProc();

    String getDBMSName();

    SQLConnectionPoolConfig getConfig();
    Wrappers getWrappers();
    SQLReader createReader();

}
