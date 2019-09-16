package ru.bio4j.ng.database.api;

import java.sql.SQLException;

/**
 *
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLActionScalar1<P, R> {
    R exec(SQLContext context, P param) throws SQLException;
}
