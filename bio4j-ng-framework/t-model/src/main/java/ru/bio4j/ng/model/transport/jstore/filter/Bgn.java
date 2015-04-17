package ru.bio4j.ng.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class Bgn extends Compare {

    /**
     * Значение поля начинается со строки
     * @param fieldName
     * @param value
     */
    public Bgn(String fieldName, String value, boolean ignoreCase) {
        super(fieldName, value, ignoreCase);
    }
}
