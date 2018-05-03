package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.service.api.BioCursor;
import ru.bio4j.ng.service.api.BioHttpRequestProcessor;
import ru.bio4j.ng.service.api.BioModule;
import ru.bio4j.ng.service.api.BioRouteHandler;

public interface BioAppModule extends BioModule {
    BioCursor getCursor(String bioCode) throws Exception;
    SQLContext getSQLContext() throws Exception;

    BioHttpRequestProcessor getHttpRequestProcessor(String requestType);
    BioRouteHandler getRouteHandler(String key);

}
