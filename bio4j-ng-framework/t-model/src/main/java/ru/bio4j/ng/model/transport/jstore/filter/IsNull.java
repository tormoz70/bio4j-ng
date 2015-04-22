package ru.bio4j.ng.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class IsNull extends AbstractExpression {

    private final String column;

    public IsNull(String column) {
        this.column = column;
    }

    @Override
    public String getColumn() {
        return this.column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IsNull isNull = (IsNull) o;

        return !(column != null ? !column.equals(isNull.column) : isNull.column != null);

    }

    @Override
    public int hashCode() {
        return column != null ? column.hashCode() : 0;
    }
}
