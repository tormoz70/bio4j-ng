package ru.bio4j.ng.fcloud.h2registry;

import ru.bio4j.ng.database.commons.SqlTypeConverterImpl;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

public class H2SQLTypeConverterImpl extends SqlTypeConverterImpl {

    public int read0 (Class<?> type, int stringSize, boolean isCallableStatment) {
        if ((type == String.class) || (type == Character.class)) {
            return Types.VARCHAR;
        } else if (ru.bio4j.ng.commons.converter.Types.typeIsInteger(type)) {
            return Types.NUMERIC;
        } else if (ru.bio4j.ng.commons.converter.Types.typeIsReal(type)) {
            return Types.NUMERIC;
        } else if ((type == boolean.class) || (type == Boolean.class)) {
            return Types.CHAR;
        } else if ((type == Date.class) || (type == java.sql.Date.class) || (type == java.sql.Timestamp.class)) {
            return Types.DATE;
        } else if ((type == byte[].class)||(type == Byte[].class)) {
            return Types.BLOB;
        } else if (type == ResultSet.class) {
            return Types.NULL;
        } else
            return Types.NULL;
    }

    @Override
    public int read (Class<?> type, int stringSize, boolean isCallableStatment) {
        if (type == ResultSet.class)
            return Types.OTHER;
         else
            return this.read0(type, stringSize, isCallableStatment);
    }

}
