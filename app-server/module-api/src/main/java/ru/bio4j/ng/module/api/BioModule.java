package ru.bio4j.ng.module.api;

import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.service.api.BioCursor;

import java.io.IOException;

public interface BioModule {
    String getDescription();
    BioCursor getCursor(String bioCode) throws Exception;
    BioCursor getCursor(BioRequest request) throws Exception;
    SQLContext getSQLContext() throws Exception;
}
