package ru.bio4j.ng.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class Contains extends Compare {

    /**
     * Значение поля содержит строку
     * @param fieldName
     * @param value
     */
    public Contains(String fieldName, String value, boolean ignoreCase) {
        super(fieldName, value, ignoreCase);
    }
}
