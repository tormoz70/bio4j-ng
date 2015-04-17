package ru.bio4j.ng.model.transport;

public enum MetaType {
    UNDEFINED, STRING, INTEGER, DECIMAL, DATE, BOOLEAN, BLOB, CLOB, CURSOR;

    public static MetaType decode(String name) {
        if (name != null) {
            for (MetaType type : values()) {
                if (type.name().equals(name.toUpperCase()))
                    return type;
            }
        }
        return UNDEFINED;
    }

}
