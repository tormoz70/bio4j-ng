package ru.bio4j.ng.database.api;

public interface TotalsWrapper {

    BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef) throws Exception;
}
