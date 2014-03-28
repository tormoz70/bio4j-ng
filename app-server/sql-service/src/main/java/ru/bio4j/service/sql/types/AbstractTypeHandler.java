package ru.bio4j.service.sql.types;

public abstract class AbstractTypeHandler<T> implements TypeHandler<T> {

    private String metaType;

    public String getMetaType() {
        return metaType;
    }

    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }
}
