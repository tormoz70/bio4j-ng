package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.ABean;

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
    private ABean data;

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

    public ABean getData() {
        return data;
    }

    public void setData(ABean data) {
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
