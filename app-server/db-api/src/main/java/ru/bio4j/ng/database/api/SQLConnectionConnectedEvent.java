package ru.bio4j.ng.database.api;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 */
public interface SQLConnectionConnectedEvent {
    void handle(SQLContext sender, SQLConnectionConnectedEventAttrs attrs) throws SQLException;
}
