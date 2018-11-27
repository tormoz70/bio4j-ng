package ru.bio4j.ng.service.types;

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

        return securityService.getUser(qprms.stoken, qprms.remoteIP, qprms.remoteClient);
    }

    public User login(BioQueryParams qprms) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

//        BioRequestFactory factory = new BioRequestFactory.Login();
//        BioRequest request = factory.restore(qprms, BioRoute.LOGIN.getClazz(), null);
        return securityService.login(qprms.login, qprms.remoteIP, qprms.remoteClient);
    }

    public void logoff(BioQueryParams qprms) throws Exception {
//        if(qprms.requestType == null || !qprms.requestType.equalsIgnoreCase(BioRoute.LOGOUT.getAlias()))
//            throw new IllegalArgumentException(String.format("prms.requestType must be \"%s\"!", BioRoute.LOGOUT.getAlias()));
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        securityService.logoff(qprms.stoken, qprms.remoteIP);
    }

}
