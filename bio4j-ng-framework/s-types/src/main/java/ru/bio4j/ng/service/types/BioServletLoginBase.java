package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class BioServletLoginBase extends BioServletBase {
    protected final static Logger LOG = LoggerFactory.getLogger(BioServletLoginBase.class);

    private BioLoginProcessor loginProcessor = new BioLoginProcessor();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    protected BioRespBuilder.LoginBilder doLogin(HttpServletRequest request) throws Exception {
        BioRespBuilder.LoginBilder brsp =  BioRespBuilder.loginBuilder();
        if(securityProvider == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        loginProcessor.setSecurityProvider(securityProvider);
        SrvcUtils.BioQueryParams prms = SrvcUtils.decodeBioQueryParams(request);

        User usr = loginProcessor.login(prms);
        brsp.user(usr)
            .exception((brsp.getUser() != null ? null : new BioError.Login.BadLogin()));
        return brsp;
    }
}
