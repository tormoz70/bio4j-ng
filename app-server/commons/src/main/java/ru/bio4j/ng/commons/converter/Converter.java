package ru.bio4j.ng.commons.converter;

/**
 * @author ayrat
 *	Конвертер типов
 */
public class Converter {

    public static <T> T toType(Object value, Class<T> type) throws ConvertValueException {
        if(type == null)
            throw new IllegalArgumentException("type");
        if(value != null) {
            value = Types.wrapPrimitive(value);
            Class<?> srcType = value.getClass();
            TypeHandler<T> h = TypeHandlerMapper.getHandler(srcType);
            return h.write((T) value, type);
        } else {
            TypeHandler<T> h = TypeHandlerMapper.getHandler(type);
            return h.read(value, type);
        }
    }
}

