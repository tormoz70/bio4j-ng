package ru.bio4j.ng.model.transport.jstore;

import java.util.List;

/**
 * Строка с данными
 */
public class StoreRow {

    /**
     * используется при добавлении новой записи
     */
    private String internalROWUID;

    /**
     * Тип изменения
     */
    private RowChangeType changeType;

    /**
     * Значения в строках
     */
    private List<Object> values;

    public String getInternalROWUID() {
        return internalROWUID;
    }

    public void setInternalROWUID(String internalROWUID) {
        this.internalROWUID = internalROWUID;
    }

    public RowChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(RowChangeType changeType) {
        this.changeType = changeType;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public Object getValue(int col) {
        return values.get(col);
    }

    public void setValue(int col, Object value) {
        values.set(col, value);
    }

}
