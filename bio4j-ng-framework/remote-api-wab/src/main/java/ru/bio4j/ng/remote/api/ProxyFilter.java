package ru.bio4j.ng.remote.api;

import ru.bio4j.ng.service.types.BioWrappedRequest;
import ru.bio4j.ng.service.types.WarSecurityFilterBase;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProxyFilter extends WarSecurityFilterBase implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        try {
            BioWrappedRequest rereq = new BioWrappedRequest(req);
            rereq.putHeader("Access-Control-Allow-Origin", "*");
            ((HttpServletResponse)response).setHeader("Access-Control-Allow-Origin", "*");
            super.doFilter(rereq, response, chain);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}
