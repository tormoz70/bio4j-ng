package ru.bio4j.ng.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class End extends Compare {

    /**
     * Значение поля оканчивается на строку
     * @param fieldName
     * @param value
     */
    public End(String fieldName, String value, boolean ignoreCase) {
        super(fieldName, value, ignoreCase);
    }
}
