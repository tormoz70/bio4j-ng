package ru.bio4j.service.sql.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.func.Function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DBUtils.class);

    private DBUtils() {}

    /**
     * @title Закрытие соединения с базой данных
     * @param c
     */
    public static void close(Connection c) {
        if(c == null) {
            return;
        }
        try {
            c.close();
        } catch(Throwable t) {
            LOG.error("can't close connection", t);
        }
    }

    /**
     * @title Освобождение объекта, используемого для выполнения sql-выражений, и всех связанных с ним системных ресурсов
     * @param c
     */
    public static void close(Statement c) {
        if(c == null) {
            return;
        }
        try {
            c.close();
        } catch(Throwable t) {
            LOG.error("can't close statement", t);
        }
    }

    /**
     * @title Освобождение объекта - таблицы данных - представляющего результирующий набор данных из базы данных
     * table of data representing a database result set
     * @param c
     */
    public static void close(ResultSet c) {
        if(c == null) {
            return;
        }
        try {
            c.close();
        } catch(Throwable t) {
            LOG.error("can't close resultSet", t);
        }
    }

    public static <T> T objectFromResultSet(
            ResultSet rs,
            Function<ResultSet, T> c) throws SQLException {
        T t = null;
        if(rs.next()) {
            t = c.apply(rs);
        }
        return t;
    }

    public static <T> List<T> listFromResultSet(
            ResultSet rs,
            Function<ResultSet, T> c) throws SQLException {
        List<T> list = new ArrayList<>();
        while(rs.next()) {
            T t = c.apply(rs);
            list.add(t);
        }
        return Collections.unmodifiableList(list);
    }
}
