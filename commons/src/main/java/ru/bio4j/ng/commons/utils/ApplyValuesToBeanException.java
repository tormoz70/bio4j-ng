package ru.bio4j.ng.commons.utils;

public class ApplyValuesToBeanException extends RuntimeException {
    public ApplyValuesToBeanException() {
        super();
        field = null;
    }
    private final String field;

    public ApplyValuesToBeanException(String field, String message, Exception parentException) {
        super(message, parentException);
        this.field = field;
    }

    public ApplyValuesToBeanException(String field, String message) {
        this(field, message, null);
    }

    public String getField() {
        return field;
    }
}
