package ru.bio4j.ng.commons.converter.hanlers;

import ru.bio4j.ng.commons.converter.*;
import ru.bio4j.ng.commons.utils.Strings;

import java.lang.reflect.Array;

public class StringHandler extends TypeHandlerBase implements TypeHandler<String> {

//    @Override
//    public String read(Object value, Class<?> targetType) throws ConvertValueException {
//        if (value == null)
//            return null;
//        value = Types.wrapPrimitive(value);
//        Class<?> valType = (value == null) ? null : value.getClass();
//
//        if (Types.typeIsDate(valType))
//            return value.toString();
//        else if (Types.typeIsNumber(valType))
//            return value.toString();
//        else if (valType == String.class)
//            return (String) value;
//        else if (valType == Character.class)
//            return (String) value;
//        else if (valType.isArray() && Types.isPrimitiveOrWrapper(valType.getComponentType())){
//            Object[] valItems = (Object[])value;
//            StringBuilder rslt = new StringBuilder();
//            for (Object valItem : valItems)
//                Strings.append(rslt, valItem != null ? valItem.toString() : "", ",");
//            return rslt.toString();
//        }
//        throw new ConvertValueException(value, valType, genericType);
//    }

    @Override
    public <T> T write(String value, Class<T> targetType) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (Types.typeIsDate(targetTypeWrapped))
            return (T) Types.date2Date(Types.parsDate(value), targetTypeWrapped);
        else if (targetTypeWrapped == Boolean.class)
            return (T) Types.parsBoolean(value);
        else if (Types.typeIsNumber(targetTypeWrapped)) {
            if(Strings.isNullOrEmpty(value))
                return null;
            return (T) Types.string2Number(value, targetTypeWrapped);
        } else if (targetTypeWrapped == String.class)
            return (T) value;
        else if (targetTypeWrapped == Character.class)
            return (T) value;
        else if (targetTypeWrapped == byte[].class)
            return (T) value.getBytes();
        else if (targetTypeWrapped.isEnum())
            return (T) Types.parsEnum(value, targetTypeWrapped);
        else if (targetTypeWrapped.isArray() && Types.isPrimitiveOrWrapper(targetTypeWrapped.getComponentType())){
            String[] valStrs = Strings.split(value, ",");
            T array = (T) Array.newInstance(targetTypeWrapped.getComponentType(), valStrs.length);
            for (int i=0; i<valStrs.length; i++)
                Array.set(array, i, Converter.toType(valStrs[i], targetTypeWrapped.getComponentType()));
            return array;
        }

        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(String value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
