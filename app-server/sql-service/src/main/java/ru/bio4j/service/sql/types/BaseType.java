package ru.bio4j.service.sql.types;

public class BaseType<T> implements Type<T> {

    private final String name;

    private final Class<T> type;

    public BaseType(Class<T> type, String name) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getJavaType() {
        return type;
    }
}
