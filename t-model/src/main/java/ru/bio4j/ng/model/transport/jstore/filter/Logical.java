package ru.bio4j.ng.model.transport.jstore.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Элемент фильтра
 */

public class Logical extends Expression {

    public Logical() {
        super();
    }

    public Logical(Expression ... expressions) {
        super(expressions);
    }

}
