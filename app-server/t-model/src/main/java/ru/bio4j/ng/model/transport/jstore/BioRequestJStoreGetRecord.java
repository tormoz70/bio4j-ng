package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;

import java.util.List;

/**
 * Запрос на получение данных в JStoreClient
 */
public class BioRequestJStoreGetRecord extends BioRequest {

    /**
     * Значение первичного ключя записи, которую надо загрузить на клиент
     * Примечание: используется для загрузки данных в форму редактирования, например.
     */
    private Object id;

    private String origJson;

    public String getOrigJson() {
        return origJson;
    }

    public void setOrigJson(String origJson) {
        this.origJson = origJson;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

}

