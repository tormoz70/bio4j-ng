package ru.bio4j.ng.model.transport.jstore.filter;

import java.util.List;

public abstract class AbstractExpression implements Expression {

    // вернет or,not и пр
    private final String selfName = getClass().getSimpleName().toLowerCase();

    @Override
    public String getName() {
        throw new NoSuchMethodError("Not implemented for "+this.getClass().getName());
    }
    @Override
    public Object getValue() {
        throw new NoSuchMethodError("Not implemented for "+this.getClass().getName());
    }

    @Override
    public Expression add(Expression expression) {
        throw new NoSuchMethodError("Not implemented for "+this.getClass().getName());
    }

    @Override
    public boolean ignoreCase() {
        throw new NoSuchMethodError("Not implemented for "+this.getClass().getName());
    }

    @Override
    public List<?> getChildrens() {
        throw new NoSuchMethodError("Not implemented for "+this.getClass().getName());
    }

    @Override
    public String getColumn() {
        throw new NoSuchMethodError("Not implemented for "+this.getClass().getName());
    }


}
