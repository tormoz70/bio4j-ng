package ru.bio4j.ng.model.transport.jstore.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Элемент фильтра
 */

public class Logical extends AbstractExpression {

    protected final List<Expression> operands;

    public Logical(Expression ... operands) {
        if(operands.length > 0)
            this.operands = Arrays.asList(operands);
        else
            this.operands = new ArrayList<>();
    }

    @Override
    public List<Expression> getChildrens() {
        return Collections.unmodifiableList(this.operands);
    }

    @Override
    public Expression add(Expression expression) {
        this.operands.add(expression);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Logical)) return false;

        Logical logical = (Logical) o;

        return !(operands != null ? !operands.equals(logical.operands) : logical.operands != null);

    }

    @Override
    public int hashCode() {
        return operands != null ? operands.hashCode() : 0;
    }
}
