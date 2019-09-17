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
            if(targetType.getComponentType() == Array.get(value, 0).getClass())
                return (T)value;
            T targetArray = (T) Array.newInstance(targetType.getComponentType(), Array.getLength(value));
            if(Array.getLength(targetArray) == 0)
                return targetArray;
            for (int i=0; i<Array.getLength(targetArray); i++) {
                Object val = Converter.toType(Array.get(value, i), targetType.getComponentType());
                try {
                    Array.set(targetArray, i, val);
                } catch (IllegalArgumentException e) {
                    throw new ConvertValueException(val, val != null ? val.getClass() : null, targetType.getComponentType());
                }
            }
            return targetArray;
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
