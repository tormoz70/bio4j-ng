package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;

/**
 * Запрос на получение данных в JStoreClient
 */
public class BioRequestJStoreGet extends BioRequest {

    /**
     * Начальная позиция
     */
    private int offset;

    /**
     * Размер страницы
     */
    private int pagesize;

    /**
     * Параметры сортировки для запросов GET
     */
    private Sort sort;

    /**
     * Параметры фильтрации для запросов GET
     */
    private Expression filter;

    /**
     * Значение первичного ключа для запросов GET(с разбиением по страницам) если надо установить курсор в нужную позицию
     * Примечание: необходимо использовать в сочетании с offset
     */
    private Object location;

    private String origJson;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
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

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public String getOrigJson() {
        return origJson;
    }

    public void setOrigJson(String origJson) {
        this.origJson = origJson;
    }
}

