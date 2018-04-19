package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.sql.SQLException;
import java.util.List;

public interface Wrappers {

    FilteringWrapper getFilteringWrapper();

    SortingWrapper getSortingWrapper();

    PaginationWrapper getPaginationWrapper();

    TotalsWrapper getTotalsWrapper();

    LocateWrapper getLocateWrapper();

    GetrowWrapper getGetrowWrapper();
}
