package ru.bio4j.ng.commons.converter;

/**
 * @author ayrat
 *	Конвертер типов
 */
public class Converter {



    public static <T> T toType(Object value, Class<T> type, String format, Boolean silent) {
        if(type == null)
            throw new IllegalArgumentException("type");
        if(type == Object.class) {
            if (silent) {
                try {
                    return (T) value;
                } catch(Exception e) {
                    return null;
                }
            } else
                return (T) value;
        }
        if(value != null) {
            value = Types.wrapPrimitive(value);
            Class<?> srcType = value.getClass();
            TypeHandler<T> h = TypeHandlerMapper.getHandler(srcType);
            if(h == null)
                throw new IllegalArgumentException(String.format("TypeHandler not found for %s!", srcType.getName()));

            if (silent) {
                try {
                    return h.write((T) value, type, format);
                } catch(Exception e) {
                    return null;
                }
            } else
                return h.write((T) value, type, format);

        } else {
            TypeHandler<T> h = TypeHandlerMapper.getHandler(type);
            if(h == null)
                throw new IllegalArgumentException(String.format("TypeHandler not found for %s!", type.getName()));

            if (silent) {
                try {
                    return h.read(value, type);
                } catch(Exception e) {
                    return null;
                }
            } else
                return h.read(value, type);
        }
    }

    public static <T> T toType(Object value, Class<T> type, String format) {
        return toType(value, type, format, false);
    }

    public static <T> T toType(Object value, Class<T> type) {
        return toType(value, type, null, false);
    }

    public static <T> T toType(Object value, Class<T> type, Boolean silent) {
        return toType(value, type, null, silent);
    }

}

