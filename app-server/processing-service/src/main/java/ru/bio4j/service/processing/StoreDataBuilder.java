package ru.bio4j.service.processing;

import ru.bio4j.model.transport.jstore.StoreData;
import ru.bio4j.model.transport.jstore.StoreRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoreDataBuilder {

    private int offset;
    private int pageSize;
    private StoreMetadataBuilder metadataBuilder;
    private final List<StoreRow> rows = new ArrayList<>();
    private Map<String,Integer> fieldToIndex;

    public StoreDataBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public StoreDataBuilder pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public StoreDataBuilder metadata(StoreMetadataBuilder metadataBuilder) {
        this.metadataBuilder = metadataBuilder;
        return this;
    }

    public StoreDataBuilder row(List<Object> row) {
        final StoreRow storeRow = new StoreRow();
        storeRow.setValues(row);
        this.rows.add(storeRow);
        return this;
    }

    public StoreDataBuilder fieldToIndex(Map<String, Integer> fieldToIndex) {
        this.fieldToIndex = fieldToIndex;
        return this;
    }

    public StoreData build() {
        return new StoreData(offset, pageSize, metadataBuilder.build(), rows, fieldToIndex);
    }
}