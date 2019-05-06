package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.service.api.SecurityService;
import ru.bio4j.ng.service.api.LoginProcessor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class WarSecurityFilterBase {
    private Logger LOG;

    private boolean bioDebug = false;
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

    public final static String SCFG_PARAM_NAME_BIODEBUG = "bioDebug";

    public void init(FilterConfig filterConfig) throws ServletException {
        LOG = LoggerFactory.getLogger(this.getClass());
        debug("init...");
        if (filterConfig != null) {
            bioDebug = Strings.compare(filterConfig.getInitParameter(SCFG_PARAM_NAME_BIODEBUG), "true", true);
            errorPage = filterConfig.getInitParameter("error_page");
            debug(" Security filter config : {" +
                  "   -- bioDebug : {}\n"+
                  "   -- errorPage : {}\n" +
                  " }", bioDebug, errorPage);
        }
        debug("init - done.");
    }

    private LoginProcessor loginProcessor;
    protected SecurityService securityService;
    protected void initSecurityHandler(ServletContext servletContext) throws Exception {
        if(securityService == null) {
            try {
                securityService = Utl.getService(servletContext, SecurityService.class);
            } catch (IllegalStateException e) {
                securityService = null;
            }
        }
        if (securityService == null) {
            throw new BioError("Security provider not defined!");
        }
        loginProcessor = securityService.createLoginProcessor();
        if(loginProcessor == null)
            loginProcessor = new DefaultLoginProcessor(securityService);
    }

    public void doSequrityFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final FilterChain chn = chain;
        final HttpServletResponse resp = (HttpServletResponse) response;
        resp.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        final WrappedRequest req = (WrappedRequest) request;
        final String servletPath = req.getServletPath();
        final HttpSession session = req.getSession();

        //GET,POST,PUT,DELETE,PATCH,HEAD
        if(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD").contains(req.getMethod())) {
            try {
                debug("Do filter for sessionId, servletPath, request: {}, {}, {}", session.getId(), servletPath, req);
                initSecurityHandler(req.getServletContext());
                String pathInfo = req.getPathInfo();
                if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/login", false)) {
                    if(loginProcessor.doLogin(req, resp))
                        chn.doFilter(req, resp);
                } else if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/curusr", false)) {
                    if(loginProcessor.doGetUser(req, resp))
                        chn.doFilter(req, resp);
                } else if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/logoff", false)) {
                    if(loginProcessor.doLogoff(req, resp))
                        chn.doFilter(req, resp);
                } else {
                    if(loginProcessor.doOthers(req, resp))
                        chn.doFilter(req, resp);
                }
            } catch (BioError.Login e) {
                log_error("Authentication error (Level-0)!", e);
                ErrorHandler.getInstance().writeError(e, resp);
            } catch (Exception e) {
                log_error("Unexpected error while filtering (Level-1)!", e);
                ErrorHandler.getInstance().writeError(e, resp);
            }
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        try {
            WrappedRequest rereq = new WrappedRequest(req);
            rereq.putHeader("Access-Control-Allow-Origin", "*");
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Origin", "*");
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Credentials", "true");
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH");
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Credentials, Origin, X-Requested-With, Content-Type, Accept, X-SToken, X-Pagination-Current-Page, X-Pagination-Per-Page, Authorization");
            ((HttpServletResponse) response).setHeader("Access-Control-Expose-Headers", "Content-Disposition, X-Suggested-Filename");
            doSequrityFilter(request, response, chain);
        } catch (Exception ex) {
            LOG.error(null, ex);
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Origin", "*");
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Credentials", "true");
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH");
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Credentials, Origin, X-Requested-With, Content-Type, Accept, X-SToken, X-Pagination-Current-Page, X-Pagination-Per-Page, Authorization");
            ((HttpServletResponse) response).setHeader("Access-Control-Expose-Headers", "Content-Disposition, X-Suggested-Filename");
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void destroy() {
        debug("Trying destroy");
    }
}
