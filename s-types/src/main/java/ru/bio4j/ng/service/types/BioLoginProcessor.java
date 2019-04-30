package ru.bio4j.ng.service.types;

import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;

public class BioLoginProcessor {

    private BioSecurityService securityService;

    public void setSecurityService(BioSecurityService securityService) {
        this.securityService = securityService;
    }

    public User getUser(BioQueryParams qprms) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

        return securityService.getUser(qprms);
    }

    public User login(BioQueryParams qprms) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

        return securityService.login(qprms);
    }

    public void logoff(final BioQueryParams qprms) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        securityService.logoff(qprms);
    }

}
