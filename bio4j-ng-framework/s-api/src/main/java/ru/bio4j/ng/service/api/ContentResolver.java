package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;

public interface ContentResolver {

//    SQLContext getSQLContext(String moduleKey) throws Exception;

//    BioSQLDefinition getCursor(String moduleKey, String bioCode, User usr) throws Exception;
    BioSQLDefinition getCursor(String moduleKey, String bioCode) throws Exception;
    BioSQLDefinition getCursor(String moduleKey, BioRequest bioRequest) throws Exception;

}
