package ru.bio4j.ng.service.types;

import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;

public class BioLoginProcessor {

    private SecurityProvider securityProvider;

    public void setSecurityProvider(SecurityProvider securityProvider) {
        this.securityProvider = securityProvider;
    }

    public User getUser(BioQueryParams qprms) throws Exception {
        if(securityProvider == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

        return securityProvider.getUser(qprms.stoken, qprms.remoteIP, qprms.remoteClient);
    }

    public User login(BioQueryParams qprms) throws Exception {
        if(securityProvider == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

//        BioRequestFactory factory = new BioRequestFactory.Login();
//        BioRequest request = factory.restore(qprms, BioRoute.LOGIN.getClazz(), null);
        return securityProvider.login(qprms.login, qprms.remoteIP, qprms.remoteClient);
    }

    public void logoff(BioQueryParams qprms) throws Exception {
//        if(qprms.requestType == null || !qprms.requestType.equalsIgnoreCase(BioRoute.LOGOUT.getAlias()))
//            throw new IllegalArgumentException(String.format("prms.requestType must be \"%s\"!", BioRoute.LOGOUT.getAlias()));
        if(securityProvider == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        securityProvider.logoff(qprms.stoken, qprms.remoteIP);
    }

}
