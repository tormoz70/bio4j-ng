package ru.bio4j.ng.service.types;

//import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.*;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.service.api.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class DefaultLoginProcessorImpl implements LoginProcessor {

    private SecurityApi securityApi;
    private ErrorWriter errorWriter;
    private BioConfig config;

    public void init(BioConfig config, SecurityApi securityApi) {
        this.config = config;
        this.securityApi = securityApi;
        ErrorWriterType errorWriterType = Utl.enumValueOf(ErrorWriterType.class, config.getUseLoginErrorHandler(), ErrorWriterType.Json);
        errorWriter = errorWriterType.createImpl();
    }

    private void _doGetUser(final HttpServletRequest request, final HttpServletResponse response) {
        User user = securityApi.doGetUser(request);
        ABean result = SrvcUtils.buildSuccess(user);
        try {
            response.getWriter().append(Jecksons.getInstance().encode(result));
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    private void _doLogin(final HttpServletRequest request, final HttpServletResponse response) {
        User user = securityApi.doLogin(request);
        ABean result = SrvcUtils.buildSuccess(user);
        try {
            response.getWriter().append(Jecksons.getInstance().encode(result));
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    private void _doLogoff(final HttpServletRequest request, final HttpServletResponse response) {
        securityApi.doLogoff(request);
        ABean result = SrvcUtils.buildSuccess(null);
        try {
            response.getWriter().append(Jecksons.getInstance().encode(result));
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    private void doOthers(final HttpServletRequest request, final HttpServletResponse response) {
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        User user;
        if (!Strings.isNullOrEmpty(qprms.login))
            user = securityApi.doLogin(request);
        else
            user = securityApi.doGetUser(request);
        req.setUser(user);
        ServletContextHolder.setCurrentUser(user);
    }

    public void process(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
        if(config.getUseLoginProcessingHandler()) {
            final HttpServletRequest reqs = (HttpServletRequest) request;
            final HttpServletResponse resp = (HttpServletResponse) response;
            resp.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            final String servletPath = reqs.getServletPath();
            final HttpSession session = reqs.getSession();
            try {
                //debug("Do filter for sessionId, servletPath, request: {}, {}, {}", session.getId(), servletPath, request);
                String pathInfo = reqs.getPathInfo();
                if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/login", false)) {
                    _doLogin(reqs, resp);
                } else if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/curusr", false)) {
                    _doGetUser(reqs, resp);
                } else if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/logoff", false)) {
                    _doLogoff(reqs, resp);
                } else {
                    doOthers(reqs, resp);
                    chain.doFilter(request, resp);
                }
            } catch (Exception e) {
                errorWriter.write(e, resp);
            }
        }
    }

}
