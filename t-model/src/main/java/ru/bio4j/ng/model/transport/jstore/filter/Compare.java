package ru.bio4j.ng.model.transport.jstore.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Элемент фильтра
 */

public class Compare extends Expression {

    private final String column;
    private final Object value;
    private final boolean ignoreCase;

    public Compare(String column, Object value, boolean ignoreCase) {
        this.column = column;
        this.value = value;
        this.ignoreCase = ignoreCase;
    }
    public Compare(String column, Object value) {
        this(column, value, false);
    }

    public Compare() {
        this(null, null, false);
    }

    @Override
    public String getColumn() {
        return this.column;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public boolean ignoreCase() {
        return this.ignoreCase;
    }
}
