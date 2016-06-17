package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;

public class BioLoginProcessor {

    private SecurityProvider securityProvider;

    public void setSecurityProvider(SecurityProvider securityProvider) {
        this.securityProvider = securityProvider;
    }

    public User getUser(SrvcUtils.BioQueryParams prms) throws Exception {
        if(securityProvider == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

        return securityProvider.getUser(prms.uid, prms.remoteIP);
    }

    public User login(SrvcUtils.BioQueryParams qprms) throws Exception {
        if(qprms.requestType == null || !qprms.requestType.equalsIgnoreCase(BioRoute.LOGIN.getAlias()))
            throw new IllegalArgumentException(String.format("prms.requestType must be \"%s\"!", BioRoute.LOGIN.getAlias()));
        if(securityProvider == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

        BioRequestFactory factory = BioRoute.LOGIN.getFactory();
        BioRequest request = factory.restore(qprms, BioRoute.LOGIN.getClazz(), null);
        return securityProvider.login(request.getLogin(), qprms.remoteIP);
    }

    public void logoff(SrvcUtils.BioQueryParams qprms) throws Exception {
        if(qprms.requestType == null || !qprms.requestType.equalsIgnoreCase(BioRoute.LOGOUT.getAlias()))
            throw new IllegalArgumentException(String.format("prms.requestType must be \"%s\"!", BioRoute.LOGOUT.getAlias()));
        if(securityProvider == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

        securityProvider.logoff(qprms.uid, qprms.remoteIP);
    }

}
