package ru.bio4j.ng.model.transport.jstore;

import java.util.List;

/**
 * Метаданные пакета данных
 */
public class StoreMetadata {
    /**
     * Редактируемо пользователем (может быть преопределено в колонке)
     */
    private boolean readonly;

    /**
     * Включен режим мультиселекта
     */
    private boolean multiSelection;

    /**
     * Имя id-поля
     */
    private String idProperty;

    /**
     * Описание полей
     */
    private List<Field> fields;

    public boolean getReadonly() {
        return readonly;
    }

    public boolean getMultiSelection() {
        return multiSelection;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
        if(this.idProperty == null && this.fields != null && this.fields.size() > 0) {
            for(Field field : this.fields)
                if(field.isPk()) {
                    this.idProperty = field.getName().toLowerCase();
                    break;
                }
        }
    }

    public String getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(String idProperty) {
        this.idProperty = idProperty;
    }
}
