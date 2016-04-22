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
import ru.bio4j.ng.service.api.SecurityProvider;

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

    private void processBadLoginError(final HttpServletResponse resp) throws IOException {
        BioError.Login.BadLogin e = new BioError.Login.BadLogin();
        BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(e), resp, bioDebug);
        log_error("An error while checking User!", e);

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
            final SrvcUtils.BioQueryParams prms = SrvcUtils.decodeBioQueryParams(req);
            final boolean weAreInPublicAreas = detectWeAreInPublicAreas(prms.bioCode);
            if (securityProvider != null) {
                User user = loginProcessor.login(prms);
                if (user.isAnonymous() && !weAreInPublicAreas) {
                    debug("Anonymous not in public area for bioCode \"{}\"!", prms.bioCode);
                    processBadLoginError(resp);
                } else {
                    HttpServletRequest wrappedRequest = processUser(user, req, resp);
                    chn.doFilter(wrappedRequest, resp);
                }
            } else {
                Exception e = new Exception("Security provider not defined!");
                BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(BioError.wrap(e)), resp, bioDebug);
                log_error("An error while checking User!", e);
            }

        } catch (Exception e) {
            BioServletBase.writeError(BioRespBuilder.anErrorBuilder().exception(BioError.wrap(e)), resp, bioDebug);
            log_error("Unexpected error while filtering (Level-1)!", e);
        }
    }

    @Override
    public void destroy() {
        debug("Trying destroy");
    }
}
