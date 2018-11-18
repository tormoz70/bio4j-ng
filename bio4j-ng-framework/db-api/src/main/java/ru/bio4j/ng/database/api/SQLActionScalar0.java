package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;

/**
 *
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLActionScalar0<R> {
    R exec(SQLContext context) throws Exception;
}
