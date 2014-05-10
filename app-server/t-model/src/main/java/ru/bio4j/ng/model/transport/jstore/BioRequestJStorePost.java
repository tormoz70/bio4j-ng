package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.BioRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Запрос на сохранение изменений в JStoreClient
 */
public class BioRequestJStorePost extends BioRequest {

    /**
     * Измененный пакет данных (в случае POST)
     */
    private StoreData packet;

    /**
     * Может быть передано несколько запросов BioRequestJStorePost
     * выходные параметры каждого из запросов
     * должны добавляться во входные парамеры последующего
     */
    private final List<BioRequestJStorePost> bioRequests = new ArrayList<>();

    public StoreData getPacket() {
        return packet;
    }

    public void setPacket(StoreData packet) {
        this.packet = packet;
    }

    public List<BioRequestJStorePost> getBioRequests() {
        return bioRequests;
    }
}

