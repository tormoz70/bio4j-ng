package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.BioRespBuilder;

import javax.servlet.http.HttpServletRequest;

public class BioServletLoginBase extends BioServletBase {
    protected final static Logger LOG = LoggerFactory.getLogger(BioServletLoginBase.class);

    protected BioRespBuilder.Login doLogin(HttpServletRequest request) throws Exception {
        BioRespBuilder.Login brsp =  BioRespBuilder.login();
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        String login = request.getParameter(BioServletBase.UID_PARAM_NAME);
        brsp.user(securityHandler.getUser(login))
            .success(brsp.getUser() != null);
        return brsp;
    }
}