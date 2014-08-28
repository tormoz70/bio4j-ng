package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface SQLCursor extends SQLCommand, AutoCloseable {
    /**
     * Инициализировать курсор
     * @param conn - соединение
     * @param sql - предложение
     * @param prms - параметры
     * @param timeout - время ожидания ответа в сек (по умолчанию 60 сек)
     * @return ссылка на этот же курсор
     * @throws SQLException
     */
    SQLCursor init(Connection conn, String sql, List<Param> prms, int timeout) throws Exception;
    /**
     * Инициализировать курсор
     * @param conn - соединение
     * @param sql - предложение
     * @param prms - параметры
     * @return ссылка на этот же курсор
     * @throws SQLException
     */
    SQLCursor init(Connection conn, String sql, List<Param> prms) throws Exception;

    String getSQL();

    SQLCursor open(List<Param> params) throws Exception;
    SQLCursor open() throws Exception;
    boolean isActive();

    SQLReader reader();
}
