package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.jstore.filter.Filter;


public interface FilteringWrapper {
    String wrap(String sql, Filter filter) throws Exception;
}
