package ru.bio4j.ng.database.api;

/**
 *
 * @param <R> - тип возвращаемого параметра
 */
public interface SQLActionScalar1<P, R> {
    R exec(SQLContext context, P param) throws Exception;
}
