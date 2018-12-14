package ru.bio4j.ng.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class Bgn extends Compare {

    public Bgn(String column, Object value, boolean ignoreCase) {
        super(column, value, ignoreCase);
    }
    public Bgn(String column, Object value) {
        this(column, value, false);
    }

    public Bgn() {
        this(null, null, false);
    }
}
