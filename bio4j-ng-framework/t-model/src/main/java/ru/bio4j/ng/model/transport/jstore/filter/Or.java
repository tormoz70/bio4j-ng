package ru.bio4j.ng.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class Or extends Logical {

    public Or(Expression ... operands) {
        super(operands);
    }
}
