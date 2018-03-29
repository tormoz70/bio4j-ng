package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;

/**
 *
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLActionScalar<R> {
    R exec(SQLContext context, Connection conn, User usr) throws Exception;
}
