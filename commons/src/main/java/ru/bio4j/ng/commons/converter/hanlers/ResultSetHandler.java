package ru.bio4j.ng.commons.converter.hanlers;

import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.TypeHandler;
import ru.bio4j.ng.commons.converter.TypeHandlerBase;
import ru.bio4j.ng.commons.converter.Types;

import java.sql.ResultSet;

public class ResultSetHandler extends TypeHandlerBase implements TypeHandler<ResultSet> {

    @Override
    public ResultSet read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value instanceof ResultSet)
            return (ResultSet)value;
        value = Types.wrapPrimitive(value);
        Class<?> valType = (value == null) ? null : value.getClass();
        throw new ConvertValueException(value, valType, genericType);
    }

    @Override
    public <T> T write(ResultSet value, Class<T> targetType) throws ConvertValueException {
        if(targetType == ResultSet.class)
            return (T)value;
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(ResultSet value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
