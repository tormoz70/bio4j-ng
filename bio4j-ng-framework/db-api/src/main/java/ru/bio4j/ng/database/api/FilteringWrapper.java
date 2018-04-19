package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.jstore.filter.Filter;


public interface FilteringWrapper {
    BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef, Filter filter) throws Exception;
}
