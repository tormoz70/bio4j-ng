package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.User;

public interface SecurityHandler extends BioService {
    User getUser(String moduleKey, String loginOrUid) throws Exception;
    void logoff(String uid) throws Exception;
}
