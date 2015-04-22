package ru.bio4j.ng.database.oracle.impl;

import ru.bio4j.ng.database.commons.DbReader;

import java.sql.*;

/**
 * Created by ayrat on 24.04.14.
 */
public class OraReader extends DbReader {
    public OraReader(ResultSet resultSet) {
        super(resultSet);
    }

}
