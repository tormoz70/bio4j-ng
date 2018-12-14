package ru.bio4j.ng.model.transport;

import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.util.List;

/**
 * Пгинация
 */
public interface BioRequestPagination {

    Integer getTotalCount();

    void setTotalCount(Integer totalCount);

    Integer getPage();

    void setPage(Integer page);

    Integer getOffset();

    void setOffset(Integer offset);

    Integer getLimit();

    void setLimit(Integer limit);

    List<Sort> getSort();

    void setSort(List<Sort> sort);

    Filter getFilter();

    void setFilter(Filter filter);
}

