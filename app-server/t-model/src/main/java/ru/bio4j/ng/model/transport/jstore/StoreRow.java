package ru.bio4j.ng.model.transport.jstore;

import java.util.Map;

/**
 * Строка с данными
 */
public class StoreRow {

    /**
     * используется при добавлении новой записи
     */
    private String internalId;

    /**
     * Тип изменения
     */
    private RowChangeType changeType;

    /**
     * Значения в строках
     */
    private Map<String, Object> data;

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public RowChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(RowChangeType changeType) {
        this.changeType = changeType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Object getValue(String name) {
        if(data == null)
            throw new IllegalArgumentException("Attribute \"data\" is null!");
        return data.get(name.toLowerCase());
    }

    public void setValue(String name, Object value) {
        if(data == null)
            throw new IllegalArgumentException("Attribute \"data\" is null!");
        data.put(name, value);
    }

}
