package ru.bio4j.ng.database.api;

import java.sql.*;

public interface SQLContext {
    //Connection getConnection() throws SQLException;
    //Connection getConnection(String userName, String password) throws SQLException;

    <C, R> R execBatch (final SQLAction<C, R> batch, final C context) throws Exception;
    <R> R execBatch (final SQLActionScalar<R> batch) throws Exception;
    <S, C, T> T execBatch (final S scope, final SQLActionExt<S, C, T> batch, final C context) throws Exception;
    <S, C, T> T execSQLAtomic(final S scope, final Connection conn, final SQLActionExt<S, C, T> action, final C context) throws Exception;
    <C, R> R execSQLAtomic(final Connection conn, final SQLAction<C, R> action, final C context) throws Exception;
    <R> R execSQLAtomic(final Connection conn, final SQLActionScalar<R> action) throws Exception;

//    SQLConnectionPoolStat getStat();
    void addAfterEvent(SQLConnectionAfterEvent e);
    void clearAfterEvents();

    SQLCursor CreateCursor();
    SQLStoredProc CreateStoredProc();
    SQLReader CreateReader(ResultSet resultSet);

    String getDBMSName();
}
