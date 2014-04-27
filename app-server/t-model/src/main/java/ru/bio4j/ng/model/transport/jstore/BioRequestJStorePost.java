package ru.bio4j.ng.model.transport.jstore;

/**
 * Запрос на сохранение изменений в JStoreClient
 */
public class BioRequestJStorePost extends BioRequestJStore {

    /**
     * Измененный пакет данных (в случае POST)
     */
    private StoreData packet;

    public StoreData getPacket() {
        return packet;
    }

    public void setPacket(StoreData packet) {
        this.packet = packet;
    }
}

