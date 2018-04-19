package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.api.SQLContext;

public interface BioAppModule extends BioModule {
//    BioCursorDeclaration getCursor(String bioCode, User user) throws Exception;
    BioCursorDeclaration getCursor(String bioCode) throws Exception;
//    BioCursorDeclaration getCursor(BioRequest request) throws Exception;
    SQLContext getSQLContext() throws Exception;

    BioHttpRequestProcessor getHttpRequestProcessor(String requestType);
    BioRouteHandler getRouteHandler(String key);

}
