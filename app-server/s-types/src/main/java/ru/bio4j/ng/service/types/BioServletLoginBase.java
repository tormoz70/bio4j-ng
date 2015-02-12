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

//    private Set<String> publicAreas = new HashSet();
//
//    private void initPublicAreas(String publicArea) {
//        publicAreas.clear();
//        publicAreas.addAll(Arrays.asList(Strings.split(publicArea, ' ', ',', ';')));
//    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        String publicArea = "bio@users"; //servletConfig.getInitParameter(BioServletBase.SCFG_PARAM_NAME_PUBLIC_AREAS);
        loginProcessor.initPublicAreas(publicArea);
//        initPublicAreas(publicArea);
    }

//    private boolean detectWeAreInPublicAreas(String bioCode) {
//        return publicAreas.contains(bioCode);
//    }

    protected BioRespBuilder.Login doLogin(HttpServletRequest request) throws Exception {
        BioRespBuilder.Login brsp =  BioRespBuilder.login();
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        loginProcessor.setSecurityHandler(securityHandler);
        BioQueryParams prms = decodeBioQueryParams(request);
//        final boolean weAreInPublicAreas = Strings.isNullOrEmpty(prms.bioCode) || detectWeAreInPublicAreas(prms.bioCode);
//        if(weAreInPublicAreas)
//            prms.loginOrUid = BioServletBase.BIO_ANONYMOUS_USER_LOGIN;
//
//        final String uid = prms.loginOrUid.contains("/") ? null : prms.loginOrUid;
//        final String login = prms.loginOrUid.contains("/") ? prms.loginOrUid : null;
//
//        User usr = securityHandler.getUser(prms.moduleKey, uid);
//        if(usr == null)
//            securityHandler.login(prms.moduleKey, login);

        User usr = loginProcessor.login(prms);
        brsp.user(usr)
            .exception((brsp.getUser() != null ? null : new BioError.Login.BadLogin()));
        return brsp;
    }
}
