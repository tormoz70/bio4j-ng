package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.jstore.Sort;
import java.util.List;

public interface SortingWrapper {

    BioCursorDeclaration.SelectSQLDef wrap(BioCursorDeclaration.SelectSQLDef sqlDef, List<Sort> sort) throws Exception;
}
