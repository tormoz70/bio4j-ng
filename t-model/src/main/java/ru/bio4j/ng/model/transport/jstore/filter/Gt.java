package ru.bio4j.ng.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class Gt extends Compare {

    public Gt(String fieldName, Object value) {
        super(fieldName, value);
    }
    public Gt() {
        this(null, null);
    }
}
