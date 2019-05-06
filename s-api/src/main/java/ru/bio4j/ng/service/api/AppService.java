package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;

public interface AppService {
    SQLDefinition getSQLDefinition(String bioCode) throws Exception;
    HttpParamMap getHttpParamMap() throws Exception;
    SQLContext getSQLContext() throws Exception;
}
