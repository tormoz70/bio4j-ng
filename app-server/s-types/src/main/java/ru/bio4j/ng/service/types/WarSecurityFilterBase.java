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
import java.util.Map;
import java.util.TreeMap;

public class WarSecurityFilterBase implements Filter {
    private Logger LOG;

    private boolean bioDebug = false;
    private String forwardURL = null;
    private String errorPage;

    private void error(String s, Throwable e) {
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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG = LoggerFactory.getLogger(this.getClass());
        debug("init...");
        if (filterConfig != null) {
            bioDebug = Strings.compare(filterConfig.getInitParameter(BioServletBase.BIODEBUG_PARAM_NAME), "true", true);
            forwardURL = filterConfig.getInitParameter(BioServletBase.FORWARD_URL_PARAM_NAME);
            errorPage = filterConfig.getInitParameter("error_page");
            debug("errorPage - {}.", errorPage);
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

    private void processUser(User user, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (user != null) {
            Map<String, String[]> extraParams = new TreeMap<>();
            extraParams.putAll(request.getParameterMap());
            extraParams.put(BioServletBase.UID_PARAM_NAME, new String[] {user.getUid()});
            HttpServletRequest wrappedRequest = new BioWrappedRequest(request, extraParams);
            chain.doFilter(wrappedRequest, response);
        } else
            BioServletBase.writeError(BioRespBuilder.anError().exception(new BioError.Login.BadLogin()), response, bioDebug);

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final FilterChain chn = chain;
        final HttpServletResponse resp = (HttpServletResponse) response;
        final HttpServletRequest req = (HttpServletRequest) request;
        final String servletPath = req.getServletPath();
        final HttpSession session = req.getSession();

        try {

            debug("Do filter for sessionId, servletPath, request: {}, {}, {}", session.getId(), servletPath, req);

            initSecurityHandler(req.getServletContext());
            final String moduleKey = req.getParameter(BioServletBase.MODULE_PARAM_NAME);
            final String uid = req.getParameter(BioServletBase.UID_PARAM_NAME);
            if(securityHandler != null) {
                User user = securityHandler.getUser(moduleKey, uid);
                processUser(user, req, resp, chn);
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
                                processUser(user, req, resp, chn);
                            } else {
                                BioServletBase.writeError(BioRespBuilder.anError().exception(bresp.getException()), resp, bioDebug);
                                error("An error while checking User!", bresp.getException());
                            }
                        }
                    });
                } catch (Exception e) {
                    throw new BioError(String.format("Unexpected error while forwarding getUser-request to the BioServer! Message: %s", e.getMessage()), e);
                }

            }

        } catch (Exception e) {
            BioServletBase.writeError(BioRespBuilder.anError().exception(BioError.wrap(e)), resp, bioDebug);
            error("Unexpected error while filtering (Level-1)!", e);
        }
    }

    @Override
    public void destroy() {
        debug("Trying destroy");
    }
}
