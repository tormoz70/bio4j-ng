package ru.bio4j.ng.database.api;

import java.sql.Connection;

/**
 *
 */
public interface SQLActionVoid {
    void exec(SQLContext context, Connection conn) throws Exception;
}
