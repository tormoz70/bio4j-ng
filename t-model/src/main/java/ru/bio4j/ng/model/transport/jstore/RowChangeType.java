package ru.bio4j.ng.model.transport.jstore;

/**
 * Тип изменения строки
 */
public enum RowChangeType {
    unchanged(0), create(1), update(2), delete(3);

    private final int code;
    private RowChangeType(int code) {
        this.code = code;
    }

    private int getCode() {
        return code;
    }

}
