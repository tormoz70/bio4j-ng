package ru.bio4j.ng.database.pgsql.impl;

import oracle.jdbc.OracleTypes;
import ru.bio4j.ng.database.commons.SqlTypeConverterImpl;

import java.sql.ResultSet;

public class PgSQLTypeConverterImpl extends SqlTypeConverterImpl {

    @Override
    public int read (Class<?> type, int stringSize, boolean isCallableStatment) {
        if (type == ResultSet.class)
            return OracleTypes.CURSOR;
         else
            return super.read(type, stringSize, isCallableStatment);
    }

}