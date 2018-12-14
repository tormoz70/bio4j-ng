package ru.bio4j.ng.model.transport;

import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.util.List;

/**
 * Запрос на получение Json
 */
public class BioRequestGetJson extends BioRequest implements BioRequestPagination {

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
    private Integer pageSize;

    /**
     * Параметры сортировки для запросов GET
     */
    private List<Sort> sort;

    /**
     * Параметры фильтрации для запросов GET
     */
    private Filter filter;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return pageSize;
    }

    public void setLimit(Integer limit) {
        this.pageSize = limit;
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

    @Override
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}

