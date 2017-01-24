package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.User;

public interface SecurityProvider extends BioService {
    User getUser(final String stoken, final String remoteIP) throws Exception;
    User login(final String login, final String remoteIP) throws Exception;
    Boolean loggedin(final String stoken) throws Exception;
    void logoff(final String stoken, final String remoteIP) throws Exception;
}
