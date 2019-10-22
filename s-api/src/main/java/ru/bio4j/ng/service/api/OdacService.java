package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLDefinition;

public interface OdacService {
    SQLDefinition getSQLDefinition(String bioCode);
    SQLContext getSQLContext();
}
