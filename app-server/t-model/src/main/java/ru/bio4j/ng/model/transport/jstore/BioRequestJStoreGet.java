package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.jstore.filter.Expression;

/**
 * Запрос на получение данных в JStoreClient
 */
public class BioRequestJStoreGet extends BioRequestJStore {

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
     * Параметры для запросов GET если надо установить курсор в нужную позицию
     * Примечание: необходимо использовать в сочетании с offset
     */
    private Expression location;


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
}

