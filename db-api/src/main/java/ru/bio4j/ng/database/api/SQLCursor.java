package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public interface SQLCursor extends SQLCommand {
    SQLCursor init(final Connection conn, final String sql, final List<Param> prms, final int timeout) throws Exception;
    SQLCursor init(final Connection conn, final String sql, final List<Param> prms) throws Exception;
    SQLCursor init(final Connection conn, final String sql) throws Exception;

    String getSQL();

    SQLReader createReader();

    boolean fetch(final List<Param> params, final User usr, final DelegateSQLFetch onrecord) throws Exception;
    boolean fetch(final User usr, final DelegateSQLFetch onrecord) throws Exception;

    public <T> T scalar(final List<Param> params, final User usr, final String fieldName, final Class<T> clazz, T defaultValue) throws Exception;
    public <T> T scalar(final List<Param> params, final User usr, final Class<T> clazz, T defaultValue) throws Exception;
    public <T> T scalar(final User usr, final String fieldName, final Class<T> clazz, T defaultValue) throws Exception;
    public <T> T scalar(final User usr, final Class<T> clazz, T defaultValue) throws Exception;

    <T> List<T> beans(final List<Param> params, final User usr, final Class<T> clazz) throws Exception;
    <T> List<T> beans(final User usr, final Class<T> clazz) throws Exception;
    <T> T firstBean(final List<Param> params, final User usr, final Class<T> clazz) throws Exception;
    <T> T firstBean(final User usr, final Class<T> clazz) throws Exception;

}
