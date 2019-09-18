package ru.bio4j.ng.database.api;


import java.sql.*;
import java.util.List;


public interface SQLNamedParametersStatement extends Statement {

    String getParamsAsString();

    String getOrigQuery();
    String getParsedQuery();

    void setObjectAtName(String name, Object value) throws SQLException;
    void setObjectAtName(String name, Object value, int targetSqlType) throws SQLException;
    void setStringAtName(String name, String value) throws SQLException;
    void setIntAtName(String name, int value) throws SQLException;
    void setLongAtName(String name, long value) throws SQLException;
    void setTimestampAtName(String name, Timestamp value) throws SQLException;
    void setDateAtName(String name, Date value) throws SQLException;
    void setNullAtName(String name) throws SQLException;
    void registerOutParameter(String paramName, int sqlType) throws SQLException;
    Object getObject(String paramName) throws SQLException;
    PreparedStatement getStatement();
    boolean execute() throws SQLException;

    ResultSet executeQuery(String sql) throws SQLException;
    ResultSet executeQuery() throws SQLException;
    int executeUpdate(String sql) throws SQLException;
    int executeUpdate() throws SQLException;

    List<String> getParamNames() throws SQLException;
    void setQueryTimeout(int seconds) throws SQLException;

}