package ru.bio4j.ng.database.api;

public interface PaginationWrapper {

    BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef) throws Exception;
}
