package ru.bio4j.ng.database.api;


public interface GetrowWrapper {
    BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef) throws Exception;
}
