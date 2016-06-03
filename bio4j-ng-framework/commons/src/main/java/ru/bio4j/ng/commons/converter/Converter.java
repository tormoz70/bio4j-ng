package ru.bio4j.ng.commons.converter;

/**
 * @author ayrat
 *	Конвертер типов
 */
public class Converter {

    public static <T> T toType(Object value, Class<T> type, String format) throws ConvertValueException {
        if(type == null)
            throw new IllegalArgumentException("type");
        if(type == Object.class)
            return (T)value;
        if(value != null) {
            value = Types.wrapPrimitive(value);
            Class<?> srcType = value.getClass();
            TypeHandler<T> h = TypeHandlerMapper.getHandler(srcType);
            if(h == null)
                throw new IllegalArgumentException(String.format("TypeHandler not found for %s!", srcType.getName()));
            return h.write((T) value, type, format);
        } else {
            TypeHandler<T> h = TypeHandlerMapper.getHandler(type);
            if(h == null)
                throw new IllegalArgumentException(String.format("TypeHandler not found for %s!", type.getName()));
            return h.read(value, type);
        }
    }

    public static <T> T toType(Object value, Class<T> type) throws ConvertValueException {
        return toType(value, type, null);
    }

}

