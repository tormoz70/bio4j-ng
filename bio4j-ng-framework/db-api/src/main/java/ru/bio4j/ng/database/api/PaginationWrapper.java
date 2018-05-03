package ru.bio4j.ng.database.api;

public interface PaginationWrapper {

    String wrap(String sql) throws Exception;
}
