package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.database.api.BioCursor;

public interface BioModule {
    String getDescription();
    BioCursor getCursor(String bioCode) throws Exception;
    SQLContext getSQLContext() throws Exception;
}
