package ru.bio4j.ng.database.api;


import java.sql.*;
import java.util.List;


public interface SQLNamedParametersStatement extends Statement {

    String getParamsAsString();

    String getOrigQuery();
    String getParsedQuery();

    void setObjectAtName(String name, Object value);
    void setObjectAtName(String name, Object value, int targetSqlType);
    void setStringAtName(String name, String value);
    void setIntAtName(String name, int value);
    void setLongAtName(String name, long value);
    void setTimestampAtName(String name, Timestamp value);
    void setDateAtName(String name, Date value);
    void setNullAtName(String name);
    void registerOutParameter(String paramName, int sqlType);
    Object getObject(String paramName);
    PreparedStatement getStatement();
    boolean execute() throws SQLException;

    ResultSet executeQuery(String sql);
    ResultSet executeQuery();
    int executeUpdate(String sql);
    int executeUpdate();

    List<String> getParamNames();
    void setQueryTimeout(int seconds);

}