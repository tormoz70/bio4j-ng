package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.SecurityProvider;

public class BioLoginProcessor {

    private SecurityProvider securityProvider;

    public void setSecurityProvider(SecurityProvider securityProvider) {
        this.securityProvider = securityProvider;
    }

    public User login(SrvcUtils.BioQueryParams prms) throws Exception {
        BioRespBuilder.LoginBilder brsp =  BioRespBuilder.loginBuilder();
        if(securityProvider == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

        final String uid = prms.loginOrUid.contains("/") ? null : prms.loginOrUid;
        final String login = prms.loginOrUid.contains("/") ? prms.loginOrUid : null;

        User usr;
        if(!Strings.isNullOrEmpty(uid)) {
            usr = securityProvider.getUser(uid, prms.remoteIP);
        } else {
            usr = securityProvider.login(login, prms.remoteIP);
        }
        return usr;
    }

}
