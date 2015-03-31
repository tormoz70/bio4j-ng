package ru.bio4j.ng.database.pgsql.impl;

import ru.bio4j.ng.database.commons.SqlTypeConverterImpl;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

public class PgSQLTypeConverterImpl extends SqlTypeConverterImpl {

    public int read0 (Class<?> type, int stringSize, boolean isCallableStatment) {
        if ((type == String.class) ||
                (type == Character.class)) {
            return (stringSize <= (isCallableStatment ? 32000 : 4000)) ? java.sql.Types.VARCHAR : java.sql.Types.CLOB;
        } else if (ru.bio4j.ng.commons.converter.Types.typeIsInteger(type)) {
            return java.sql.Types.INTEGER;
        } else if (ru.bio4j.ng.commons.converter.Types.typeIsReal(type)) {
            return java.sql.Types.NUMERIC;
        } else if ((type == boolean.class) || (type == Boolean.class)) {
            return java.sql.Types.CHAR;
        } else if ((type == Date.class) || (type == java.sql.Date.class) || (type == java.sql.Timestamp.class)) {
            return java.sql.Types.DATE;
        } else if ((type == byte[].class)||(type == Byte[].class)) {
            return java.sql.Types.BLOB;
        } else if (type == ResultSet.class) {
            return java.sql.Types.NULL;
        } else
            return java.sql.Types.NULL;
    }

    @Override
    public int read (Class<?> type, int stringSize, boolean isCallableStatment) {
        if (type == ResultSet.class)
            return Types.OTHER;
         else
            return this.read0(type, stringSize, isCallableStatment);
    }

}
