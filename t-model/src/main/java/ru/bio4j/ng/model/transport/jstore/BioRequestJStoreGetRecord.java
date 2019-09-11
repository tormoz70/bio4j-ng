package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.BioRequest;

/**
 * Запрос на получение данных в JStoreClient
 */
public class BioRequestJStoreGetRecord extends BioRequest {

    /**
     * Значение первичного ключа записи, которую надо загрузить на клиент
     * Примечание: используется для загрузки данных в форму редактирования, например.
     */
    private Object id;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

}

