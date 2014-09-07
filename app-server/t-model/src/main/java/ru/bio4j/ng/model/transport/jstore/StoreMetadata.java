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
    }
}
