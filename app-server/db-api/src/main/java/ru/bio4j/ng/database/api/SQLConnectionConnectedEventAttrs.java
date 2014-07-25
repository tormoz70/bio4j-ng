package ru.bio4j.ng.database.api;

import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 27.11.13
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class SQLConnectionConnectedEventAttrs {
    private Connection connection;

    public static SQLConnectionConnectedEventAttrs build (Connection connection) {
        SQLConnectionConnectedEventAttrs rslt = new SQLConnectionConnectedEventAttrs();
        rslt.connection = connection;
        return rslt;
    }

    public Connection getConnection() {
        return this.connection;
    }

}
