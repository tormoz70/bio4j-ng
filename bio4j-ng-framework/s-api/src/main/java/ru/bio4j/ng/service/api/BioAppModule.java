package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;

public interface BioAppModule extends BioModule {
    BioSQLDefinition getSQLDefinition(String bioCode) throws Exception;
    SQLContext getSQLContext() throws Exception;

    BioHttpRequestProcessor getHttpRequestProcessor(String requestType);
    BioRouteHandler getRouteHandler(String key);

}
