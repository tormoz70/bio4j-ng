package ru.bio4j.ng.model.transport;

import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.util.List;

public class FilterAndSorter {
    private List<Sort> sorter;
    private Filter filter;

    public List<Sort> getSorter() {
        return sorter;
    }

    public void setSorter(List<Sort> sorter) {
        this.sorter = sorter;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
