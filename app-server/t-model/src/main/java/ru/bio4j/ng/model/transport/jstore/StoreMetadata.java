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
    private List<Column> columns;

    public boolean getReadonly() {
        return readonly;
    }

    public boolean getMultiSelection() {
        return multiSelection;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
