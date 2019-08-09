package ru.bio4j.ng.commons.converter.hanlers;

import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.TypeHandler;
import ru.bio4j.ng.commons.converter.TypeHandlerBase;
import ru.bio4j.ng.commons.converter.Types;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BooleanHandler extends TypeHandlerBase implements TypeHandler<Boolean> {

    private static final List<String> trueStrings = Arrays.asList("true", "t", "yes", "y", "1", "on");
    private static final List<Character> trueChars = Arrays.asList('t','T','y','Y','1');
    private static Boolean pars(String value) {
        return trueStrings.contains(value.toLowerCase());
    }
    private static Boolean pars(Character value) {
        return trueChars.contains(value);
    }

    @Override
    public Boolean read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value == null) {
            return targetType.isPrimitive() ? false : null;
        }
        value = Types.wrapPrimitive(value);
        Class<?> valType = (value == null) ? null : value.getClass();

        if (valType == Date.class)
            Types.nop();
        else if (valType == java.sql.Date.class)
            Types.nop();
        else if (valType == java.sql.Timestamp.class)
            Types.nop();
        else if (Types.typeIsNumber(valType))
            return Types.number2Number((Number) value, long.class) > 0;
        else if (valType == String.class)
            return pars((String) value);
        else if (valType == Character.class)
            return pars((Character) value);

        throw new ConvertValueException(value, valType, genericType);
    }

    @Override
    public <T> T write(Boolean value, Class<T> targetType) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (targetTypeWrapped == Date.class)
            Types.nop();
        else if (targetTypeWrapped == java.sql.Date.class)
            Types.nop();
        else if (targetTypeWrapped == java.sql.Timestamp.class)
            Types.nop();
        else if (targetTypeWrapped == Boolean.class)
            return (T) value;
        else if (Types.typeIsNumber(targetTypeWrapped))
            return (T) Types.number2Number((value ? 1 : 0), targetTypeWrapped);
        else if (targetTypeWrapped == String.class)
            return (T) (value ? "1" : "0");
        else if (targetTypeWrapped == Character.class)
            return (T) (value ? "1" : "0");
        else if (targetTypeWrapped == byte[].class)
            Types.nop();
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(Boolean value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
