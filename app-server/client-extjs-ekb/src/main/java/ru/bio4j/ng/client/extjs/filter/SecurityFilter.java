package ru.bio4j.ng.client.extjs.filter;

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
import ru.bio4j.ng.service.types.BioServletBase;
import ru.bio4j.ng.service.types.BioWrappedRequest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class SecurityFilter implements Filter {
    private final static Logger LOG = LoggerFactory.getLogger(SecurityFilter.class);

    private String forwardURL = null;
    private String errorPage;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.debug("init...");
        if (filterConfig != null) {
            forwardURL = filterConfig.getInitParameter(BioServletBase.FORWARD_URL_PARAM_NAME);
            errorPage = filterConfig.getInitParameter("error_page");
            LOG.debug("errorPage - {}.", errorPage);
        }
        LOG.debug("init - done.");
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
        final HttpSession session = request.getSession();
        if (user != null) {
            session.setAttribute(BioServletBase.UID_SESSION_ATTR_NAME, user.getUid());
            Map<String, String[]> extraParams = new TreeMap<>();
            extraParams.putAll(request.getParameterMap());
            if(extraParams.containsKey(BioServletBase.LOGIN_PARAM_NAME))
                extraParams.put(BioServletBase.LOGIN_PARAM_NAME, new String[] {"."});
            extraParams.put(BioServletBase.UID_PARAM_NAME, new String[] {user.getUid()});
            HttpServletRequest wrappedRequest = new BioWrappedRequest(request, extraParams);
            chain.doFilter(wrappedRequest, response);
        } else
            BioServletBase.writeResponse(BioRespBuilder.anError().addError(new BioError.Login.BadLogin()), response);

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final FilterChain chn = chain;
        final HttpServletResponse resp = (HttpServletResponse) response;
        final HttpServletRequest req = (HttpServletRequest) request;
        final String servletPath = req.getServletPath();
        final HttpSession session = req.getSession();

        try {

            LOG.debug("Do filter for sessionId, servletPath, request: {}, {}, {}", session.getId(), servletPath, req);

    //        chain.doFilter(req, resp);
    //        if(true) return;


//            // Allow access to all, except biosrv.
//            if (!servletPath.equals("/biosrv")) {
//                chain.doFilter(req, resp);
//                return;
//            }


            String login = req.getParameter(BioServletBase.LOGIN_PARAM_NAME);
            initSecurityHandler(req.getServletContext());
            if(securityHandler != null) {
                User user = securityHandler.getUser(login);
                processUser(user, req, resp, chn);
            } else {
                final String queryString = Httpc.getQueryString(req);
                final String destination = this.forwardURL+(isNullOrEmpty(queryString) ? "" : "?"+queryString);
                Httpc.requestJson(destination, new Httpc.Callback() {
                    @Override
                    public void process(InputStream inputStream) throws Exception {
                        String brespJson = Utl.readStream(inputStream);
                        BioResponse bresp = Jsons.decode(brespJson, BioResponse.class);
                        if(bresp.isSuccess()) {
                            User user = bresp.getUser();
                            processUser(user, req, resp, chn);
                        } else {
                            BioServletBase.writeResponse(BioRespBuilder.anError().addError(bresp.getException()), resp);
                            LOG.error("An error while checking User!", bresp.getException());
                        }
                    }
                });

            }

        } catch (Exception e) {
            BioServletBase.writeResponse(BioRespBuilder.anError().addError(BioError.wrap(e)), resp);
            LOG.error("Unexpected error while filtering (Level-1)!", e);
        }
    }

    @Override
    public void destroy() {
        LOG.debug("Trying destroy");
    }
}
