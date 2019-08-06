package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLDefinition;

public interface AppService {
    SQLDefinition getSQLDefinition(String bioCode) throws Exception;
    SQLContext getSQLContext() throws Exception;
}
