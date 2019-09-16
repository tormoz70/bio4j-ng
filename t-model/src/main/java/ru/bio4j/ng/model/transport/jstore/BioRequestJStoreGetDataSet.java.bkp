package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.BioRequestPagination;
import ru.bio4j.ng.model.transport.BioRequestRunLongOp;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.util.List;

/**
 * Запрос на получение данных в JStoreClient
 */
public class BioRequestJStoreGetDataSet extends BioRequestRunLongOp implements BioRequestPagination {

    /**
     * Общее количество записей передается в случае кеширования данных на сервере
     */
    private Integer totalCount;

    /**
     * Страница
     */
    private Integer page;

    /**
     * Начальная позиция
     */
    private Integer offset;

    /**
     * Размер страницы
     */
    private Integer limit;

    /**
     * Параметры сортировки для запросов GET
     */
    private List<Sort> sort;

    /**
     * Параметры фильтрации для запросов GET
     */
    private Filter filter;

    /**
     * Значение первичного ключа для запросов GET(с разбиением по страницам) если надо установить курсор в нужную позицию
     * Примечание: необходимо использовать в сочетании с offset
     */
    private Object location;

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<Sort> getSort() {
        return sort;
    }

    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public Integer getPage() {
        return page;
    }

    @Override
    public void setPage(Integer page) {
        this.page = page;
    }
}

