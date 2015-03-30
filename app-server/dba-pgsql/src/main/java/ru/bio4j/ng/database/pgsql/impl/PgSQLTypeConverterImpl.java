package ru.bio4j.ng.database.pgsql.impl;

import ru.bio4j.ng.database.commons.SqlTypeConverterImpl;

import java.sql.ResultSet;
import java.sql.Types;

public class PgSQLTypeConverterImpl extends SqlTypeConverterImpl {

    @Override
    public int read (Class<?> type, int stringSize, boolean isCallableStatment) {
        if (type == ResultSet.class)
            return Types.OTHER;
         else
            return super.read(type, stringSize, isCallableStatment);
    }

}
