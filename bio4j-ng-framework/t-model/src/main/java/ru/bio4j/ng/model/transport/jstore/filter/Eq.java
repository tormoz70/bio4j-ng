package ru.bio4j.ng.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class Eq extends Compare {

    /**
     * Значение поля РАВНО значению
     * @param fieldName
     * @param value
     */
    public Eq(String fieldName, String value, boolean ignoreCase) {
        super(fieldName, value, ignoreCase);
    }

    public Eq(String fieldName, Object value) {
        super(fieldName, value, false);
    }
}
