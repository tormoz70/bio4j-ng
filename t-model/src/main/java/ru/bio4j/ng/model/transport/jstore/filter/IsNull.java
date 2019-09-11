package ru.bio4j.ng.model.transport.jstore.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Элемент фильтра
 */

public class IsNull extends Expression {

    private final String column;

    public IsNull(String column) {
        this.column = column;
    }

    public IsNull() {
        this(null);
    }

    @Override
    public String getColumn() {
        return this.column;
    }

}
