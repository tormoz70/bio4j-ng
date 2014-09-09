package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.BioRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Запрос на сохранение изменений в JStore
 */
public class BioRequestJStorePost extends BioRequest {

    /**
     * Измененный пакет данных (в случае POST)
     */
    private List<StoreRow> modified;

    /**
     * Может быть передано несколько дочерних запросов BioRequestJStorePost
     * выходные параметры каждого из запросов
     * должны добавляться во входные парамеры дочернего
     */
    private final List<BioRequestJStorePost> children = new ArrayList<>();

    public List<StoreRow> getModified() {
        return modified;
    }

    public void setModified(List<StoreRow> modified) {
        this.modified = modified;
    }

    public List<BioRequestJStorePost> getChildren() {
        return children;
    }
}

