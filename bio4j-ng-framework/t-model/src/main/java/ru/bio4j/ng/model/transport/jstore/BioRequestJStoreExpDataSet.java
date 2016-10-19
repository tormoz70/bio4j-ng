package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.util.HashMap;
import java.util.List;

/**
 * Запрос на получение данных в JStoreClient
 */
public class BioRequestJStoreExpDataSet extends BioRequest {

    /**
     * Заголовки колонок, которые надо выгрузить
     */
    private HashMap<String, String> columns;

    /**
     * Параметры сортировки для запросов EXP
     */
    private List<Sort> sort;

    /**
     * Параметры фильтрации для запросов EXP
     */
    private Filter filter;

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

    public HashMap<String, String> getColumns() {
        return columns;
    }

    public void setColumns(HashMap<String, String> columns) {
        this.columns = columns;
    }
}

