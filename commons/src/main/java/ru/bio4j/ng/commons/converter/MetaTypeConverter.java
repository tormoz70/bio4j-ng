package ru.bio4j.ng.commons.converter;

import ru.bio4j.ng.model.transport.MetaType;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

/**
 * Created by ayrat on 17.04.14.
 */
public class MetaTypeConverter {

    public static MetaType read(Class<?> type) {
        Class<?> typeWrapped = Types.wrapPrimitiveType(type);
        if(typeWrapped == String.class)
            return MetaType.STRING;
        if(Types.typeIsInteger(typeWrapped))
            return MetaType.INTEGER;
        if(Types.typeIsNumber(typeWrapped) && !Types.typeIsInteger(typeWrapped))
            return MetaType.DECIMAL;
        if(Types.typeIsDate(typeWrapped))
            return MetaType.DATE;
        if(typeWrapped == Boolean.class)
            return MetaType.BOOLEAN;
        if(typeWrapped == byte[].class)
            return MetaType.BLOB;
        if(typeWrapped == ResultSet.class)
            return MetaType.CURSOR;
        return MetaType.UNDEFINED;
    }

    public static Class<?> write(MetaType type) {
        if (type == MetaType.STRING)
            return String.class;
        if (type == MetaType.INTEGER)
            return Long.class;
        if (type == MetaType.DECIMAL)
            return BigDecimal.class;
        if (type == MetaType.DATE)
            return Date.class;
        if (type == MetaType.BOOLEAN)
            return Boolean.class;
        if (type == MetaType.BLOB)
            return byte[].class;
        if (type == MetaType.CURSOR)
            return ResultSet.class;
        return Object.class;
    }

}
