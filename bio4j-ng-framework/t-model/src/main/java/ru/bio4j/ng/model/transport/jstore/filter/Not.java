package ru.bio4j.ng.model.transport.jstore.filter;

import java.util.Collections;
import java.util.List;

/**
 * Элемент фильтра
 */

public class Not extends AbstractExpression {

    private Expression expression;

    public Not(Expression expression) {
        this.expression = expression;
    }

    @Override
    public List<?> getChildrens() {
        return Collections.singletonList(this.expression);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Not)) return false;

        Not not = (Not) o;

        return !(expression != null ? !expression.equals(not.expression) : not.expression != null);

    }

    @Override
    public int hashCode() {
        return expression != null ? expression.hashCode() : 0;
    }
}
