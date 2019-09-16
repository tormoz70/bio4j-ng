package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLActionScalar0<R> {
    R exec(SQLContext context) throws SQLException;
}
