package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 */
public interface SQLActionVoid0 {
    void exec(SQLContext context) throws SQLException;
}
