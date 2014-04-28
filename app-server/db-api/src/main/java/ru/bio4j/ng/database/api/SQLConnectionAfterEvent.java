package ru.bio4j.ng.database.api;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 */
public interface SQLConnectionAfterEvent {
    void handle(SQLContext sender, SQLConnectionAfterEventAttrs attrs) throws SQLException;
}
