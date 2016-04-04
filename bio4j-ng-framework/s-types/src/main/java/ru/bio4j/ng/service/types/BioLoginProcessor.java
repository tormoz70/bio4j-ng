package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.SecurityHandler;

public class BioLoginProcessor {

    private SecurityHandler securityHandler;

    public void setSecurityHandler(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
    }

    public User login(SrvcUtils.BioQueryParams prms) throws Exception {
        BioRespBuilder.LoginBilder brsp =  BioRespBuilder.loginBuilder();
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

        final String uid = prms.loginOrUid.contains("/") ? null : prms.loginOrUid;
        final String login = prms.loginOrUid.contains("/") ? prms.loginOrUid : null;

        User usr;
        if(!Strings.isNullOrEmpty(uid)) {
            usr = securityHandler.getUser(uid);
        } else {
            usr = securityHandler.login(login);
        }
        return usr;
    }

}
