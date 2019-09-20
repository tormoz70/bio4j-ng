package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.model.transport.User;

public interface SecurityService {
    User restoreUser(final String stokenOrUsrUid);
    User getUser(final BioQueryParams qprms);
    User login(final BioQueryParams qprms);
    void logoff(final BioQueryParams qprms);
    Boolean loggedin(final BioQueryParams qprms);
}
