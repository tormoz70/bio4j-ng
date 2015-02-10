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

    private Set<String> publicAreas = new HashSet();

    public void setSecurityHandler(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
    }

    public void initPublicAreas(String publicArea) {
        publicAreas.clear();
        if(!Strings.isNullOrEmpty(publicArea))
            publicAreas.addAll(Arrays.asList(Strings.split(publicArea, ' ', ',', ';')));
    }

    private boolean detectWeAreInPublicAreas(String bioCode) {
        return !Strings.isNullOrEmpty(bioCode) && publicAreas.contains(bioCode);
    }

    public User login(BioServletBase.BioQueryParams prms) throws Exception {
        BioRespBuilder.Login brsp =  BioRespBuilder.login();
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final boolean weAreInPublicAreas = detectWeAreInPublicAreas(prms.bioCode);
        if(weAreInPublicAreas)
            prms.loginOrUid = BioServletBase.BIO_ANONYMOUS_USER_LOGIN;

        final String uid = prms.loginOrUid.contains("/") ? null : prms.loginOrUid;
        final String login = prms.loginOrUid.contains("/") ? prms.loginOrUid : null;

        User usr = securityHandler.getUser(prms.moduleKey, uid);
        if(usr == null)
            securityHandler.login(prms.moduleKey, login);

//        brsp.user(usr)
//                .exception((brsp.getUser() != null ? null : new BioError.Login.BadLogin()));
//        return brsp;
        return usr;
    }

}
