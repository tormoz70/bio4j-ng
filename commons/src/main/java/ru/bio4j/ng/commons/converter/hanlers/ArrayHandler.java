package ru.bio4j.ng.commons.converter.hanlers;

import ru.bio4j.ng.commons.converter.*;
import ru.bio4j.ng.commons.utils.Strings;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ArrayHandler extends TypeHandlerBase implements TypeHandler<Object> {

    @Override
    public boolean isHandler(Class<?> type) {
        return type.isArray() && Types.isPrimitiveOrWrapper(type.getComponentType());
    }


    @Override
    public <T> T write(Object value, Class<T> targetType) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if(targetType.isArray()) {
            Class<?> targetArrayType = targetType.getComponentType();
            if(targetArrayType == String.class)
                return (T)value;
        } else {
            if (targetTypeWrapped == String.class) {
                StringBuilder rslt = new StringBuilder();
                for (int i=0; i<Array.getLength(value); i++)
                    Strings.append(rslt, Converter.toType(Array.get(value, i), String.class), ",");
                return (T) rslt.toString();
            }
        }
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(Object value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
