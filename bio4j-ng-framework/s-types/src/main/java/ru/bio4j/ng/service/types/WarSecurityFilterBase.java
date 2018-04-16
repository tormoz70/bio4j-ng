package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.BioRoute;
import ru.bio4j.ng.service.api.SecurityProvider;
import ru.bio4j.ng.service.api.SrvcUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class WarSecurityFilterBase {
    private Logger LOG;

    private boolean bioDebug = false;
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

    private BioLoginProcessor loginProcessor = new BioLoginProcessor();

    private void initPublicAreas(String publicArea) {
        publicAreas.clear();
        if(!Strings.isNullOrEmpty(publicArea))
            publicAreas.addAll(Arrays.asList(Strings.split(publicArea, ' ', ',', ';')));
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        LOG = LoggerFactory.getLogger(this.getClass());
        debug("init...");
        if (filterConfig != null) {
            bioDebug = Strings.compare(filterConfig.getInitParameter(BioServletBase.SCFG_PARAM_NAME_BIODEBUG), "true", true);
            initPublicAreas(filterConfig.getInitParameter(BioServletBase.SCFG_PARAM_NAME_PUBLIC_AREAS));
            errorPage = filterConfig.getInitParameter("error_page");
            debug(" Security filter config : {" +
                  "   -- bioDebug : {}\n"+
                  "   -- errorPage : {}\n" +
                  " }", bioDebug, errorPage);
        }
        debug("init - done.");
    }

    protected SecurityProvider securityProvider;
    protected void initSecurityHandler(ServletContext servletContext) {
        if(securityProvider == null) {
            try {
                securityProvider = Utl.getService(servletContext, SecurityProvider.class);
            } catch (IllegalStateException e) {
                securityProvider = null;
            }
        }
        loginProcessor.setSecurityProvider(securityProvider);
    }

    private boolean detectWeAreInPublicAreas(String bioCode) {
        return !Strings.isNullOrEmpty(bioCode) && publicAreas.contains(bioCode);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final FilterChain chn = chain;
        final HttpServletResponse resp = (HttpServletResponse) response;
        resp.setCharacterEncoding("UTF-8");
        final BioWrappedRequest req = (BioWrappedRequest) request;
        final String servletPath = req.getServletPath();
        final HttpSession session = req.getSession();

        if(req.getMethod().equals("GET") || req.getMethod().equals("POST")) {
            try {

                debug("Do filter for sessionId, servletPath, request: {}, {}, {}", session.getId(), servletPath, req);

                initSecurityHandler(req.getServletContext());
                final SrvcUtils.BioQueryParams qprms = req.getBioQueryParams();
                final boolean weAreInPublicAreas = detectWeAreInPublicAreas(qprms.bioCode);
                if (securityProvider != null) {
                    if (!Strings.isNullOrEmpty(qprms.login)) {
                        User user = loginProcessor.login(qprms);
                        BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().user(user).exception(null);
                        response.getWriter().append(responseBuilder.json());
                    } else if (qprms.requestType != null && qprms.requestType.equalsIgnoreCase(BioRoute.LOGOUT.getAlias())) {
                        loginProcessor.logoff(qprms);
                        BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().exception(null);
                        response.getWriter().append(responseBuilder.json());
                    } else {
                        User user = loginProcessor.getUser(qprms);
                        req.setUser(user);
                        if (user.isAnonymous() && !weAreInPublicAreas) {
                            debug("Anonymous not in public area for bioCode \"{}\"!", qprms.bioCode);
                            throw new BioError.Login.Unauthorized();
                        } else {
                            chn.doFilter(req, resp);
                        }
                    }
                } else {
                    throw new BioError("Security provider not defined!");
                }

            } catch (BioError.Login e) {
                log_error("Authentication error (Level-0)!", e);
//                BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(e), resp, bioDebug);
                ErrorHandler.getInstance().writeError(e, resp);
            } catch (Exception e) {
//                BioError err = BioError.wrap(e);
                log_error("Unexpected error while filtering (Level-1)!", e);
//                BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(err), resp, bioDebug);
                ErrorHandler.getInstance().writeError(e, resp);
            }
        }
    }

    public void destroy() {
        debug("Trying destroy");
    }
}
