package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.User;

public interface BioSecurityModule extends BioModule {
    User getUser(final String stoken, final String remoteIP, final String remoteClient) throws Exception;
    User login(final String login, final String remoteIP, final String remoteClient) throws Exception;
    void logoff(final String stoken, final String remoteIP) throws Exception;
    Boolean loggedin(final String stoken, final String remoteIP, final String remoteClient) throws Exception;
}
