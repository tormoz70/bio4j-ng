package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BioServletLoginBase extends BioServletBase {
    protected final static Logger LOG = LoggerFactory.getLogger(BioServletLoginBase.class);

    private BioLoginProcessor loginProcessor = new BioLoginProcessor();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    protected BioRespBuilder.Login doLogin(HttpServletRequest request) throws Exception {
        BioRespBuilder.Login brsp =  BioRespBuilder.login();
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        loginProcessor.setSecurityHandler(securityHandler);
        BioQueryParams prms = decodeBioQueryParams(request);

        User usr = loginProcessor.login(prms);
        brsp.user(usr)
            .exception((brsp.getUser() != null ? null : new BioError.Login.BadLogin()));
        return brsp;
    }
}