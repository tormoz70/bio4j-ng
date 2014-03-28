package ru.bio4j.service.processing;

import ru.bio4j.model.transport.jstore.ColumnMetadata;
import ru.bio4j.model.transport.jstore.StoreMetadata;

import java.util.ArrayList;
import java.util.List;

public class StoreMetadataBuilder {

    private boolean readOnly;
    private boolean multiSelection;
    private final List<ColumnMetadata> fields = new ArrayList<>();

    public StoreMetadataBuilder readOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public StoreMetadataBuilder multiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
        return this;
    }

    public StoreMetadataBuilder addFields(ColumnMetadata field) {
        this.fields.add(field);
        return this;
    }

    public StoreMetadata build() {
        return new StoreMetadata(readOnly, multiSelection, fields);
    }
}