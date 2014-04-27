package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;

/**
 * Ответ на запросы BioRequestJStore
 */
public class BioResponseJStore extends BioResponse {

    /**
     * Пакет данных. В ответ на BioRequestJStoreGet
     */
    private StoreData packet;

    /**
     * Сортировка, которая использовалясь при запросе к БД
     */
    private Sort sort;

    /**
     * Фильтрация, которая использовалясь при запросе к БД
     */
    private Expression filter;

    public StoreData getPacket() {
        return packet;
    }

    public void setPacket(StoreData packet) {
        this.packet = packet;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Expression getFilter() {
        return filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }
}
