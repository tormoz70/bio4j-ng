package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;

/**
 *
 */
public interface SQLActionVoid {
    void exec(SQLContext context, Connection conn, User usr) throws Exception;
}
