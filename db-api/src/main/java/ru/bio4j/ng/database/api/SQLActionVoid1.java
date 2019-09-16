package ru.bio4j.ng.database.api;

import java.sql.SQLException;

/**
 *
 */
public interface SQLActionVoid1<P> {
    void exec(SQLContext context, P param) throws SQLException;
}
