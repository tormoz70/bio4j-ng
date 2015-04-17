package ru.bio4j.ng.database.api;

import java.sql.Connection;

/**
 *
 * @param <C> - тип объекта который передается в context
 * @param <P> - тип объекта который передается в param
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLActionExt<C, P, R> {
    R exec(C context, Connection conn, P param) throws Exception;
}
