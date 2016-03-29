package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.database.api.BioCursor;
import ru.bio4j.ng.model.transport.User;

public interface BioModule extends BioService {
    void setKey(String key);
    String getKey();
    String getDescription();
    BioCursor getCursor(BioRequest bioRequest) throws Exception;
    BioCursor getCursor(String bioCode) throws Exception;
    SQLContext getSQLContext() throws Exception;
    BioHttpRequestProcessor getHttpRequestProcessor(String requestType);
}
