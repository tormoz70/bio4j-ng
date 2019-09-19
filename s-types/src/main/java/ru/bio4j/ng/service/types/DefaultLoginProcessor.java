package ru.bio4j.ng.service.types;

//import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Jecksons;
import ru.bio4j.ng.commons.utils.ServletContextHolder;
import ru.bio4j.ng.commons.utils.SrvcUtils;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class DefaultLoginProcessor implements LoginProcessor {

    private SecurityService securityService;

    public void init(SecurityService securityService) {
        this.securityService = securityService;
    }

    /***
     * обрабатывает "стандарный" запрос "/curusr"
     * @param request
     * @param response
     * @return true - продолжить обработку запроса, false - прекратить
     * @throws Exception
     */
    public boolean doGetUser(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        User user = securityService.getUser(qprms);
        ABean result = SrvcUtils.buildSuccess(user);
        response.getWriter().append(Jecksons.getInstance().encode(result));
        return false;
    }

    /***
     * обрабатывает "стандарный" запрос "/login"
     * @param request
     * @param response
     * @return true - продолжить обработку запроса, false - прекратить
     * @throws Exception
     */
    public boolean doLogin(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        User user = securityService.login(qprms);
        ABean result = SrvcUtils.buildSuccess(user);
        response.getWriter().append(Jecksons.getInstance().encode(result));
        return false;
    }

    /***
     * обрабатывает "стандарный" запрос "/logoff"
     * @param request
     * @param response
     * @return true - продолжить обработку запроса, false - прекратить
     * @throws Exception
     */
    public boolean doLogoff(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        securityService.logoff(qprms);
        ABean result = SrvcUtils.buildSuccess(null);
        response.getWriter().append(Jecksons.getInstance().encode(result));
        return false;
    }

    /***
     * обрабатывает все остальные запросы
     * @param request
     * @param response
     * @return true - продолжить обработку запроса, false - прекратить
     * @throws Exception
     */
    public boolean doOthers(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        User user = null;
        if (!Strings.isNullOrEmpty(qprms.login))
            user = securityService.login(qprms);
        else
            user = securityService.getUser(qprms);
        req.setUser(user);
        ServletContextHolder.setCurrentUser(user);
        return true;
    }

    public void processLogin(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
        final HttpServletRequest reqs = (HttpServletRequest) request;
        final HttpServletResponse resp = (HttpServletResponse) response;
        resp.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        final String servletPath = reqs.getServletPath();
        final HttpSession session = reqs.getSession();
        try {
            try {
                //debug("Do filter for sessionId, servletPath, request: {}, {}, {}", session.getId(), servletPath, request);
                String pathInfo = reqs.getPathInfo();
                if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/login", false)) {
                    if (doLogin(reqs, resp))
                        chain.doFilter(request, resp);
                } else if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/curusr", false)) {
                    if (doGetUser(reqs, resp))
                        chain.doFilter(request, resp);
                } else if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/logoff", false)) {
                    if (doLogoff(reqs, resp))
                        chain.doFilter(request, resp);
                } else {
                    if (doOthers(reqs, resp))
                        chain.doFilter(request, resp);
                }
            } catch (BioError.Login e) {
                if(RestHelper.getLoginErrorHandlerInstance() == null)
                    throw e;
                if (RestHelper.getLoginErrorHandlerInstance().writeError(e, resp)) {
                    // Ignore login error if securityErrorHandler returns true!
                    chain.doFilter(request, resp);
                }
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

}
