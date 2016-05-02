package ru.bio4j.ng.model.transport.jstore.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractExpression implements Expression {

    protected final List<Expression> children;

    public AbstractExpression() {
        this.children = new ArrayList<>();
    }

    public AbstractExpression(Expression ... expressions) {
        this();
        if(!this.children.isEmpty()) {
            this.children.clear();
        }
        for(Expression e : expressions)
            this.children.add(e);
    }

    @Override
    public List<Expression> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public Expression add(Expression expression) {
        this.children.add(expression);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Logical)) return false;

        return false;

    }

    // вернет or,not и пр
    private final String selfName = getClass().getSimpleName().toLowerCase();

    @Override
    public String getName() {
        return selfName;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public boolean ignoreCase() {
        return false;
    }

    @Override
    public String getColumn() {
        return null;
    }

    @Override
    public int hashCode() {
        return children != null ? children.hashCode() : 0;
    }

}
