package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public interface SQLCursor extends SQLCommand {
    SQLCursor init(final Connection conn, final SelectSQLDef sqlDef, final int timeout);
    SQLCursor init(final Connection conn, final SelectSQLDef sqlDef);
    SQLCursor init(final Connection conn, final String sql, final int timeout);
    SQLCursor init(final Connection conn, final String sql, final List<Param> paramDeclaration);
    SQLCursor init(final Connection conn, final String sql);

    String getSQL();

    SQLReader createReader();

    boolean fetch(final List<Param> params, final User usr, final DelegateSQLFetch onrecord);
    boolean fetch(final User usr, final DelegateSQLFetch onrecord);

    public <T> T scalar(final List<Param> params, final User usr, final String fieldName, final Class<T> clazz, T defaultValue);
    public <T> T scalar(final List<Param> params, final User usr, final Class<T> clazz, T defaultValue);
    public <T> T scalar(final User usr, final String fieldName, final Class<T> clazz, T defaultValue);
    public <T> T scalar(final User usr, final Class<T> clazz, T defaultValue);

    <T> List<T> beans(final List<Param> params, final User usr, final Class<T> clazz);
    <T> List<T> beans(final User usr, final Class<T> clazz);
    <T> T firstBean(final List<Param> params, final User usr, final Class<T> clazz);
    <T> T firstBean(final User usr, final Class<T> clazz);

}
