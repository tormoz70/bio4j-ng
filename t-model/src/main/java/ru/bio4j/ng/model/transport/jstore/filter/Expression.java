package ru.bio4j.ng.model.transport.jstore.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Expression {

    protected final List<Expression> children;

    public Expression() {
        children = new ArrayList<>();
    }

    public Expression(Expression ... expressions) {
        this();
        if(!this.children.isEmpty()) {
            this.children.clear();
        }
        for(Expression e : expressions)
            this.children.add(e);
    }

    public List<Expression> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public Expression add(Expression expression) {
        this.children.add(expression);
        return this;
    }

    public Expression addAll(List<Expression> expressions) {
        this.children.addAll(expressions);
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

    public String getName() {
        return selfName;
    }

    public Object getValue() {
        return null;
    }

    public boolean ignoreCase() {
        return false;
    }

    public String getColumn() {
        return null;
    }

    @Override
    public int hashCode() {
        return children != null ? children.hashCode() : 0;
    }

}
