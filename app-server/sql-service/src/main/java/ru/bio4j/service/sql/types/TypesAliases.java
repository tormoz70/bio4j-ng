package ru.bio4j.service.sql.types;

import ru.bio4j.collections.UnmodifiableList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypesAliases {

    public static final Type<Long> LONG = new BaseType<>(Long.class, "long");
    public static final Type<Long> INTEGER = new BaseType<>(Long.class, "int");
    public static final Type<Double> DOUBLE = new BaseType<>(Double.class, "double");
    public static final Type<Double> FLOAT = new BaseType<>(Double.class, "float");
    public static final Type<Boolean> BOOLEAN = new BaseType<>(Boolean.class, "boolean");
    public static final Type<byte[]> BYTES = new BaseType<>(byte[].class, "bytes");
    public static final Type<String> STRING = new BaseType<>(String.class, "string");
    public static final Type<Date> DATE = new BaseType<>(Date.class, "date");
    public static final Type<Object> OBJECT = new BaseType<>(Object.class, "object");

    public static final List<Type<?>> TYPES = new UnmodifiableList<>(
            BOOLEAN,
            LONG,
            INTEGER,
            DOUBLE,
            FLOAT,
            BYTES,
            DATE,
            OBJECT,
            STRING);

    private final static Map<Class<?>, Type<?>> typesByClass = new HashMap<>();
    private final static Map<String, Type<?>> typesByName = new HashMap<>();


    private static class SingletonHolder {
        public static final TypesAliases instance = new TypesAliases();
    }

    public static TypesAliases getInstance()  {
        return SingletonHolder.instance;
    }

    private TypesAliases() {
        for (Type t : TYPES) {
            typesByClass.put(t.getJavaType(), t);
            typesByName.put(t.getName(), t);
        }
    }

    public Type<?> forName(String name) {
        final Type<?> type = typesByName.get(name);
        if (type == null) {
            return OBJECT;
        }
        return type;
    }

    public String metatypeForName(String name) {
        final Type<?> type = typesByName.get(name);
        if (type == null) {
            return name;
        }
        return type.getName();
    }

    public Type<?> forClass(Class<?> clazz) {
        final Type<?> type = typesByClass.get(clazz);
        if (type == null) {
            return OBJECT;
        }
        return type;
    }
}
