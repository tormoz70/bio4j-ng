package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;

public class DbContextThreadHolder {

    private static final ThreadLocal<User> threadLocalUser = new  ThreadLocal<>();

    public final static User getCurrentUser() {
        return threadLocalUser.get();
    }

    public final static void setCurrentUser(User user) {
        threadLocalUser.set(user);
    }

    private final static ThreadLocal<Connection> threadLocalConnection = new  ThreadLocal<>();

    public final static Connection getCurrentConnection() {
        return threadLocalConnection.get();
    }

    public final static void setCurrentConnection(Connection conn) {
        threadLocalConnection.set(conn);
    }
}
