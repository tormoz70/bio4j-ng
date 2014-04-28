package ru.bio4j.ng.database.api;

import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 27.11.13
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class SQLConnectionAfterEventAttrs {
    private Connection connection;

    public static SQLConnectionAfterEventAttrs build (Connection connection) {
        SQLConnectionAfterEventAttrs rslt = new SQLConnectionAfterEventAttrs();
        rslt.connection = connection;
        return rslt;
    }

    public Connection getConnection() {
        return this.connection;
    }

}
