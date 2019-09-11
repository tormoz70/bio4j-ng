package ru.bio4j.ng.commons.converter.hanlers;

//import flexjson.factories.BigDecimalFactory;
import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.TypeHandler;
import ru.bio4j.ng.commons.converter.TypeHandlerBase;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.Param;

import java.math.BigDecimal;
import java.util.Date;

public class NumberHandler extends TypeHandlerBase implements TypeHandler<Number> {

    @Override
    public Number read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value == null) {
            if(targetType.isPrimitive())
                value = 0;
            else
                return null;
        }
        Class<? extends Number> targetTypeWrapped = (Class<? extends Number>)Types.wrapPrimitiveType(targetType);
        value = Types.wrapPrimitive(value);
        Class<?> valType = (value == null) ? null : value.getClass();

        if (Types.typeIsDate(valType))
            return Types.number2Number(((Date) value).getTime(), targetTypeWrapped);
        else if (Types.typeIsNumber(valType))
            return Types.number2Number((Number)value, targetTypeWrapped);
        else if (valType == String.class) {
            if (Strings.isNullOrEmpty((String) value))
                return null;
            return Types.number2Number(Types.parsDouble((String) value), targetTypeWrapped);
        } else if (valType == Character.class)
            return Types.number2Number(Types.parsDouble((String) value), targetTypeWrapped);
        throw new ConvertValueException(value, valType, genericType);
    }

    private BigDecimal number2bigDecimal(Number value) {
        return new BigDecimal(value.toString());
    }

    @Override
    public <T> T write(Number value, Class<T> targetType) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (targetTypeWrapped == Object.class)
            return (T) value;
        else if (Types.typeIsDate(targetTypeWrapped))
            return (T) Types.date2Date(new Date(Types.number2Number(value, long.class)), targetTypeWrapped);
        else if (targetTypeWrapped == Boolean.class) {
            return number2bigDecimal(value).compareTo(BigDecimal.valueOf(0L)) > 0 ? (T)new Boolean(true) : (T)new Boolean(false);
        } else if (Types.typeIsNumber(targetTypeWrapped))
            return (T) Types.number2Number(value, targetTypeWrapped);
        else if (targetTypeWrapped == String.class)
            return (T) value.toString();
        else if (targetTypeWrapped == Character.class)
            return (T) new Character((char)value.byteValue());
        else if (targetTypeWrapped == byte[].class)
            Types.nop();
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(Number value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
