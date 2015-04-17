package ru.bio4j.ng.model.transport.jstore.filter;

/**
 * Элемент фильтра
 */

public class Compare extends AbstractExpression {

    private final String column;
    private final Object value;
    private final boolean ignoreCase;


    public Compare(String column, Object value, boolean ignoreCase) {
        this.column = column;
        this.value = value;
        this.ignoreCase = ignoreCase;
    }
    public Compare(String column, Object value) {
        this(column, value, false);
    }

    @Override
    public String getColumn() {
        return this.column;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compare)) return false;

        Compare compare = (Compare) o;

        if (this.column != null ? !this.column.equals(compare.column) : compare.column != null) return false;
        if (this.value != null ? !this.value.equals(compare.value) : compare.value != null) return false;
        if (this.ignoreCase != compare.ignoreCase) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.column != null ? this.column.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        result = 31 * result + (this.ignoreCase ? 1 : 0);
        return result;
    }

    @Override
    public boolean ignoreCase() {
        return this.ignoreCase;
    }
}
