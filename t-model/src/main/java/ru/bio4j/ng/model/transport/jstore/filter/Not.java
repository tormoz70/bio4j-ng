package ru.bio4j.ng.model.transport.jstore.filter;

import java.util.Collections;
import java.util.List;

/**
 * Элемент фильтра
 */

public class Not extends Expression {

    public Not(Expression ... expressions) {
        super(expressions);
    }

    public Not() {
        super();
    }

}
