package ru.bio4j.ng.database.doa.impl;

import oracle.jdbc.OracleTypes;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.model.transport.MetaType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.Date;

/**
 * Default SqlTypeConverter
 */
public class SqlTypeConverter {
    public static Class<?> write (int sqlType, int charSize) {
        switch (sqlType) {
            case java.sql.Types.CHAR :
            case java.sql.Types.VARCHAR :
            case java.sql.Types.NCHAR :
            case java.sql.Types.NVARCHAR :
            case java.sql.Types.CLOB :
            case java.sql.Types.NCLOB :
            case java.sql.Types.LONGNVARCHAR :
            case java.sql.Types.LONGVARCHAR :
                return ((charSize == 1) ? Character.class : String.class);
            case java.sql.Types.BIGINT :
            case java.sql.Types.INTEGER :
                return BigInteger.class;
            case java.sql.Types.DOUBLE :
            case java.sql.Types.FLOAT :
            case java.sql.Types.DECIMAL :
            case java.sql.Types.NUMERIC :
            case java.sql.Types.REAL :
            case java.sql.Types.SMALLINT :
                return BigDecimal.class;
            case java.sql.Types.BLOB :
            case java.sql.Types.BINARY :
            case java.sql.Types.LONGVARBINARY :
            case java.sql.Types.VARBINARY :
                return Byte[].class;
            case java.sql.Types.DATE :
            case java.sql.Types.TIMESTAMP :
                return Date.class;
            default:
                return Object.class;
        }
    }
    public static int read (Class<?> type, int stringSize, boolean isCallableStatment) {
        if ((type == String.class) ||
                (type == Character.class)) {
            return (stringSize <= (isCallableStatment ? 32000 : 4000)) ? java.sql.Types.VARCHAR : java.sql.Types.CLOB;
        } else if (Types.typeIsNumber(type)) {
            return java.sql.Types.NUMERIC;
        } else if ((type == boolean.class) || (type == Boolean.class)) {
            return java.sql.Types.CHAR;
        } else if ((type == java.util.Date.class) || (type == java.sql.Date.class) || (type == java.sql.Timestamp.class)) {
            return java.sql.Types.DATE;
        } else if ((type == byte[].class)||(type == Byte[].class)) {
            return java.sql.Types.BLOB;
        } else if (type == ResultSet.class) {
            return OracleTypes.CURSOR;
        } else
            return java.sql.Types.NULL;
    }
    public static int read (MetaType type, int stringSize, boolean isCallableStatment) {
        return read (MetaTypeConverter.write(type), stringSize, isCallableStatment);
    }

}
