package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;

/**
 *
 * @param <P> - тип объекта который передается в param
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLAction<P, R> {
    R exec(SQLContext context, Connection conn, P param, User usr) throws Exception;
}
