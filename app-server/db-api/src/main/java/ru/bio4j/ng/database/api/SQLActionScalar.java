package ru.bio4j.ng.database.api;

import java.sql.Connection;

/**
 *
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLActionScalar<R> {
    R exec(SQLContext context, Connection conn) throws Exception;
}
