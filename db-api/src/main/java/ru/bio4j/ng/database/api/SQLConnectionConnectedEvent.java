package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLConnectionConnectedEvent {

    public static class Attributes {
        private User user;
        private Connection connection;

        public static Attributes build (Connection connection, User user) {
            Attributes rslt = new Attributes();
            rslt.connection = connection;
            rslt.user = user;
            return rslt;
        }

        public Connection getConnection() {
            return this.connection;
        }

        public User getUser() {
            return user;
        }
    }

    void handle(SQLContext sender, Attributes attrs) throws SQLException;
}
