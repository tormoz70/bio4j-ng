package ru.bio4j.ng.model.transport.jstore;

/**
 * Тип изменения строки
 */
public enum RowChangeType {
    UNCHANGED(0), ADDED(1), MODIFIED(2), DELETED(3);

    private final int code;
    private RowChangeType(int code) {
        this.code = code;
    }

    private int getCode() {
        return code;
    }

}
