package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.SecurityHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BioLoginProcessor {

    private SecurityHandler securityHandler;

    public void setSecurityHandler(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
    }

    public User login(BioServletBase.BioQueryParams prms) throws Exception {
        BioRespBuilder.Login brsp =  BioRespBuilder.login();
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");

        final String uid = prms.loginOrUid.contains("/") ? null : prms.loginOrUid;
        final String login = prms.loginOrUid.contains("/") ? prms.loginOrUid : null;

        User usr;
        if(!Strings.isNullOrEmpty(uid)) {
            usr = securityHandler.getUser(prms.moduleKey, uid);
        } else {
            usr = securityHandler.login(prms.moduleKey, login);
        }
        return usr;
    }

}
