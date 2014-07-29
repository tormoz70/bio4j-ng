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

    public Object getValue(int i, int j) {
        final StoreRow storeRow = rows.get(i);
        if (storeRow != null) {
            return storeRow.getValue(j);
        }
        return null;
    }

    private int indexOfColumn(String columnName) {
        if(metadata != null && metadata.getColumns() != null)
            for(int i=0; i<metadata.getColumns().size(); i++){
                Column c = metadata.getColumns().get(i);
                if(c.getName().toUpperCase().equals(columnName.toUpperCase()))
                    return i;
            }
        return -1;
    }

    public Object getValue(int row, String columnName) {
        final StoreRow storeRow = rows.get(row);
        if (storeRow != null) {
            return storeRow.getValue(indexOfColumn(columnName));
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
