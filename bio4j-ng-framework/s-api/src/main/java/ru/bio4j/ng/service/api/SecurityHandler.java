package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.User;

public interface SecurityHandler extends BioService {
    User getUser(final String uid) throws Exception;
    User login(final String login) throws Exception;
    void logoff(final String uid) throws Exception;
}
