package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.model.transport.User;

public interface SecurityService {
    User restoreUser(final String stokenOrUsrUid) throws Exception;
    User getUser(final BioQueryParams qprms) throws Exception;
    User login(final BioQueryParams qprms) throws Exception;
    void logoff(final BioQueryParams qprms) throws Exception;
    Boolean loggedin(final BioQueryParams qprms) throws Exception;
    LoginProcessor createLoginProcessor();
}
