package ru.bio4j.ng.commons.utils;

/**
 * Created by ayrat on 28.04.14.
 */
public class ApplyValuesToBeanException extends Exception {
    public ApplyValuesToBeanException() {
        super();
        field = null;
    }
    private final String field;
    public ApplyValuesToBeanException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
