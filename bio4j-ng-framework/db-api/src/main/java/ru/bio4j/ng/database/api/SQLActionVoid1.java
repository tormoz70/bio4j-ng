package ru.bio4j.ng.database.api;

/**
 *
 */
public interface SQLActionVoid1<P> {
    void exec(SQLContext context, P param) throws Exception;
}
