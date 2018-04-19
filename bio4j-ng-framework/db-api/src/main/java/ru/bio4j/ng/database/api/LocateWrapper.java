package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.jstore.Field;

public interface LocateWrapper {

    BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef, Object location) throws Exception;
}
