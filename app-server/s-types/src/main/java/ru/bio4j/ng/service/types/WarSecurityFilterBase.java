package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.SecurityHandler;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class WarSecurityFilterBase implements Filter {
    private Logger LOG;

    private boolean bioDebug = false;
    private String forwardURL = null;
    private Set<String> publicAreas = new HashSet();
    private String errorPage;

    private void log_error(String s, Throwable e) {
        if(LOG != null)
            LOG.error(s, e);
    }

    private void debug(String s, Object... objects) {
        if(LOG != null)
            LOG.debug(s, objects);
    }

    private void debug(String s) {
        debug(s, null);
    }

    private void initPublicAreas(String publicArea) {
        publicAreas.clear();
        publicAreas.addAll(Arrays.asList(Strings.split(publicArea, ' ', ',', ';')));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG = LoggerFactory.getLogger(this.getClass());
        debug("init...");
        if (filterConfig != null) {
            bioDebug = Strings.compare(filterConfig.getInitParameter(BioServletBase.SCFG_PARAM_NAME_BIODEBUG), "true", true);
            forwardURL = filterConfig.getInitParameter(BioServletBase.SCFG_PARAM_NAME_FORWARD_URL);
            String publicArea = filterConfig.getInitParameter(BioServletBase.SCFG_PARAM_NAME_PUBLIC_AREAS);
            initPublicAreas(publicArea);
            errorPage = filterConfig.getInitParameter("error_page");
            debug(" Config : {" +
                  "   -- bioDebug : {}\n"+
                  "   -- forwardURL : {}\n"+
                  "   -- publicAreas : {}\n"+
                  "   -- errorPage : {}\n" +
                  " }", bioDebug, forwardURL, publicArea, errorPage);
        }
        debug("init - done.");
    }

    protected SecurityHandler securityHandler;
    protected void initSecurityHandler(ServletContext servletContext) {
        if(securityHandler == null) {
            try {
                securityHandler = Utl.getService(servletContext, SecurityHandler.class);
            } catch (IllegalStateException e) {
                securityHandler = null;
            }
        }
    }

    private HttpServletRequest processUser(User user, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (user != null) {
            Map<String, String[]> extraParams = new TreeMap<>();
            extraParams.putAll(request.getParameterMap());
            extraParams.put(BioServletBase.QRY_PARAM_NAME_UID, new String[] {user.getUid()});
            return new BioWrappedRequest(request, extraParams);
        } else {
            BioServletBase.writeError(BioRespBuilder.anError().exception(new BioError.Login.BadLogin()), response, bioDebug);
            return request;
        }
    }

    private boolean detectWeAreInPublicAreas(String bioCode) {
        return publicAreas.contains(bioCode);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final FilterChain chn = chain;
        final HttpServletResponse resp = (HttpServletResponse) response;
        resp.setCharacterEncoding("UTF-8");
        final HttpServletRequest req = (HttpServletRequest) request;
        final String servletPath = req.getServletPath();
        final HttpSession session = req.getSession();

        try {

            debug("Do filter for sessionId, servletPath, request: {}, {}, {}", session.getId(), servletPath, req);

            initSecurityHandler(req.getServletContext());
            final String moduleKey = req.getParameter(BioServletBase.QRY_PARAM_NAME_MODULE);
            final String bioCode = req.getParameter(BioServletBase.QRY_PARAM_NAME_BIOCODE);
            final boolean weAreInPublicAreas = detectWeAreInPublicAreas(bioCode);
            final String uid = (weAreInPublicAreas ? BioServletBase.BIO_ANONYMOUS_USER_LOGIN : req.getParameter(BioServletBase.QRY_PARAM_NAME_UID));
            if (securityHandler != null) {
                User user = securityHandler.getUser(moduleKey, uid);
                HttpServletRequest wrappedRequest = processUser(user, req, resp);
                chn.doFilter(wrappedRequest, resp);
            } else {
                final String destination = String.format("%s?bm=%s&uid=%s", this.forwardURL, moduleKey, uid);
                try {
                    Httpc.requestJson(destination, new Httpc.Callback() {
                        @Override
                        public void process(InputStream inputStream) throws Exception {
                            String brespJson = Utl.readStream(inputStream);
                            BioResponse bresp = Jsons.decode(brespJson, BioResponse.class);
                            if (bresp.isSuccess()) {
                                User user = bresp.getUser();
                                HttpServletRequest wrappedRequest = processUser(user, req, resp);
                                chn.doFilter(wrappedRequest, resp);
                            } else {
                                BioServletBase.writeError(BioRespBuilder.anError().exception(bresp.getException()), resp, bioDebug);
                                log_error("An error while checking User!", bresp.getException());
                            }
                        }
                    });
                } catch (Exception e) {
                    throw new BioError(String.format("Unexpected error while forwarding getUser-request to the BioServer! Message: %s", e.getMessage()), e);
                }
            }

        } catch (Exception e) {
            BioServletBase.writeError(BioRespBuilder.anError().exception(BioError.wrap(e)), resp, bioDebug);
            log_error("Unexpected error while filtering (Level-1)!", e);
        }
    }

    @Override
    public void destroy() {
        debug("Trying destroy");
    }
}
