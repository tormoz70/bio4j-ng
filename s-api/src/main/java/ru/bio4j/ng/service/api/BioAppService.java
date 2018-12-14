package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;

public interface BioAppService {
    BioSQLDefinition getSQLDefinition(String bioCode) throws Exception;
    SQLContext getSQLContext() throws Exception;
}
