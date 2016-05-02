package ru.bio4j.ng.commons.converter.hanlers;

import ru.bio4j.ng.commons.converter.*;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteArrayHandler extends TypeHandlerBase implements TypeHandler<Byte[]> {

    @Override
    public boolean isHandler(Class<?> type) {
        return type.isArray() && (type.getComponentType() == Byte.class);
    }

    @Override
    public Byte[] read(Object value, Class<?> targetType) throws ConvertValueException {
        if (value == null)
            return null;
        Class<?> valType = (value == null) ? null : value.getClass();

        if (valType.isArray()) {
            if(valType.getComponentType() == String.class) {
                List<Byte> r = new ArrayList<>();
                for(String e : (String[])value)
                    r.add(Byte.parseByte(e));
                return (Byte[])r.toArray();
            }
            if(valType.getComponentType() == Byte.class)
                return (Byte[])value;
            if(valType.getComponentType() == byte.class)
                return Utl.toObjects((byte[]) value);
            List<String> r = new ArrayList<>();
            for (Object e : ((Object[])value)) {
                r.add(Converter.toType(e, String.class));
            }
            return (Byte[])r.toArray();
        } else if (Types.typeIsDate(valType))
            return Utl.toObjects(value.toString().getBytes());
        else if (Types.typeIsNumber(valType))
            return new Byte[] {((Number)value).byteValue()};
        else if (valType == String.class) {
            List<Byte> r = new ArrayList<>();
            String[] elems = Strings.split((String)value, new String[] {",", ";", " "});
            for(String e : elems)
                r.add(Byte.parseByte(e));
            return (Byte[])r.toArray();
        } else if (valType == Character.class)
            return new Byte[] {(Byte)value};
        throw new ConvertValueException(value, valType, genericType);
    }

    @Override
    public <T> T write(Byte[] value, Class<T> targetType) throws ConvertValueException {
        Class<?> targetTypeWrapped = Types.wrapPrimitiveType(targetType);
        if (targetType.isArray()) {
            Class<?> targetArrayType = targetType.getComponentType();
            if (targetArrayType == Byte.class)
                return (T) value;
            else if (targetArrayType == byte.class)
                return (T) Utl.toPrimitives(value);
        } else {
            if (targetTypeWrapped == String.class)
                return (T) Strings.combineArray(value, ",");
        }
        throw new ConvertValueException(value, genericType, targetTypeWrapped);
    }

    public <T> T write(Byte[] value, Class<T> targetType, String format) throws ConvertValueException {
        return write(value, targetType);
    }

}
