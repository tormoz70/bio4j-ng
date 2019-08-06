package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.model.transport.BioRequest;

public interface ContentResolver {

//    SQLContext getSQLContext(String moduleKey) throws Exception;

//    BioSQLDefinition getCursor(String moduleKey, String bioCode, User usr) throws Exception;
    SQLDefinition getCursor(String moduleKey, String bioCode) throws Exception;
    SQLDefinition getCursor(String moduleKey, BioRequest bioRequest) throws Exception;

}
