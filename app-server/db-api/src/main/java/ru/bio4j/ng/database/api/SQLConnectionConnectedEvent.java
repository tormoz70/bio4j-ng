package ru.bio4j.ng.database.api;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLConnectionConnectedEvent {

    public static class Attributes {
        private Connection connection;

        public static Attributes build (Connection connection) {
            Attributes rslt = new Attributes();
            rslt.connection = connection;
            return rslt;
        }

        public Connection getConnection() {
            return this.connection;
        }

    }

    void handle(SQLContext sender, Attributes attrs) throws SQLException;
}
