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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class WarSecurityFilterBase implements Filter {
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

    @Override
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

    private HttpServletRequest processUser(User user, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (user != null) {
            Map<String, String[]> extraParams = new TreeMap<>();
            extraParams.putAll(request.getParameterMap());
            extraParams.put(SrvcUtils.QRY_PARAM_NAME_UID, new String[] {user.getUid()});
            BioWrappedRequest rslt = new BioWrappedRequest(request);
            rslt.appendParams(extraParams);
            return rslt;
        } else {
            BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(new BioError.Login.BadLogin()), response, bioDebug);
            return request;
        }
    }

    private boolean detectWeAreInPublicAreas(String bioCode) {
        return !Strings.isNullOrEmpty(bioCode) && publicAreas.contains(bioCode);
    }

    //private void processBadLoginError(final HttpServletResponse response) throws IOException {
        //BioError.Login.BadLogin e = new BioError.Login.BadLogin();
        //BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(e), resp, bioDebug);
        //log_error("An error while checking User!", e);
    //}

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
            final SrvcUtils.BioQueryParams qprms = SrvcUtils.decodeBioQueryParams(req);
            final boolean weAreInPublicAreas = detectWeAreInPublicAreas(qprms.bioCode);
            if (securityProvider != null) {
                if(qprms.requestType != null && qprms.requestType.equalsIgnoreCase(BioRoute.LOGIN.getAlias())){
                    User user = loginProcessor.login(qprms);
                    BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().user(user).exception(null);
                    response.getWriter().append(responseBuilder.json());
                } else {
                    User user = loginProcessor.getUser(qprms);
                    if (user.isAnonymous() && !weAreInPublicAreas) {
                        debug("Anonymous not in public area for bioCode \"{}\"!", qprms.bioCode);
                        throw new BioError.Login.BadLogin();
                        //processBadLoginError(resp);
                    } else {
                        HttpServletRequest wrappedRequest = processUser(user, req, resp);
                        chn.doFilter(wrappedRequest, resp);
                    }
                }
            } else {
                throw new BioError("Security provider not defined!");
                //BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(BioError.wrap(e)), resp, bioDebug);
                //log_error("An error while checking User!", e);
            }

        } catch (BioError.Login e) {
            log_error("Authentication error (Level-0)!", e);
//            resp.setStatus(e.getErrCode());
//            resp.sendError(e.getErrCode());
//            resp.addHeader("WWW-Authenticate", "Basic realm=\"myRealm\"");
//            resp.addHeader("HTTP/1.0 401 Unauthorized", null);
            BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(BioError.wrap(e)), resp, bioDebug);
        } catch (Exception e) {
            //BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(BioError.wrap(e)), resp, bioDebug);
            BioError err = BioError.wrap(e);
            log_error("Unexpected error while filtering (Level-1)!", err);
//            resp.sendError(err.getErrCode());
            BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(BioError.wrap(e)), resp, bioDebug);
        }
    }

    @Override
    public void destroy() {
        debug("Trying destroy");
    }
}
