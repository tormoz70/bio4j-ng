package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;

public interface ContentResolver extends BioService {

    SQLContext getSQLContext(String moduleKey) throws Exception;

    BioCursor getCursor(String moduleKey, String bioCode, User usr) throws Exception;
    BioCursor getCursor(String moduleKey, String bioCode) throws Exception;
    BioCursor getCursor(String moduleKey, BioRequest bioRequest) throws Exception;

}
