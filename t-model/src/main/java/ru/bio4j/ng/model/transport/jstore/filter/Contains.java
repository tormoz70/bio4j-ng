package ru.bio4j.ng.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class Contains extends Compare {

    public Contains(String column, Object value, boolean ignoreCase) {
        super(column, value, ignoreCase);
    }
    public Contains(String column, Object value) {
        this(column, value, false);
    }

    public Contains() {
        this(null, null, false);
    }
}
