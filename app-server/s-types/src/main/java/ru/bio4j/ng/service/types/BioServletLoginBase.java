package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.api.BioRespBuilder;

import javax.servlet.http.HttpServletRequest;

public class BioServletLoginBase extends BioServletBase {
    protected final static Logger LOG = LoggerFactory.getLogger(BioServletLoginBase.class);

    protected BioRespBuilder.Login doLogin(HttpServletRequest request) throws Exception {
        BioRespBuilder.Login brsp =  BioRespBuilder.login();
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        String moduleKey = request.getParameter(BioServletBase.QRY_PARAM_NAME_MODULE);
        String login = request.getParameter(BioServletBase.QRY_PARAM_NAME_UID);
        brsp.user(securityHandler.getUser(moduleKey, login))
            .exception((brsp.getUser() != null ? null : new BioError.Login.BadLogin()));
        return brsp;
    }
}
