package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.User;

public interface SecurityHandler extends BioService {
    User getUser(String moduleKey, String loginOrUid) throws Exception;
    User login(final String moduleKey, final String login) throws Exception;
    void logoff(String moduleKey, String uid) throws Exception;
}
