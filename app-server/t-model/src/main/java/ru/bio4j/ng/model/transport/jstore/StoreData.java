package ru.bio4j.ng.model.transport.jstore;

import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Пакет данных
 */
public class StoreData {
    /**
     * Начальная позиция
     */
    private int offset;

    /**
     * Размер страницы
     */
    private int pageSize;

    /**
     * Всего записей
     */
    private int results;
    /**
     * Метаданные
     */
    private StoreMetadata metadata;

    /**
     * Строки с данными
     */
    private List<StoreRow> rows;

    public int getOffset() {
        return offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public StoreMetadata getMetadata() {
        return metadata;
    }

    public Object getValue(int row, int fieldIndex) {
        final StoreRow storeRow = rows.get(row);
        final String fieldName = metadata.getFields().get(fieldIndex).getName();
        if (storeRow != null) {
            return storeRow.getValue(fieldName);
        }
        return null;
    }

    public Object getValue(int row, String fieldName) {
        final StoreRow storeRow = rows.get(row);
        if (storeRow != null) {
            return storeRow.getValue(fieldName);
        }
        return null;
    }

    public StoreRow getRow(int i) {
        return rows.get(i);
    }

    public List<StoreRow> getRows() {
        return unmodifiableList(rows);
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setMetadata(StoreMetadata metadata) {
        this.metadata = metadata;
    }

    public void setRows(List<StoreRow> rows) {
        this.rows = rows;
    }

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }
}
