package ru.bio4j.ng.fcloud.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.types.BioWrappedRequest;
import ru.bio4j.ng.service.types.WarSecurityFilterBase;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProxyFilter extends WarSecurityFilterBase implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyFilter.class);

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
            ((HttpServletResponse)response).setHeader("Access-Control-Allow-Credentials", "true");
            ((HttpServletResponse)response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH");
            ((HttpServletResponse)response).setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Credentials, Origin, X-Requested-With, Content-Type, Accept, X-SToken, Authorization");
            super.doFilter(rereq, response, chain);
        } catch (Exception ex) {
//            throw new ServletException(ex);
            LOG.error(null, ex);
            ((HttpServletResponse)response).setHeader("Access-Control-Allow-Origin", "*");
            ((HttpServletResponse)response).setHeader("Access-Control-Allow-Credentials", "true");
            ((HttpServletResponse)response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH");
            ((HttpServletResponse)response).setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Credentials, Origin, X-Requested-With, Content-Type, Accept, X-SToken, X-Pagination-Current-Page, X-Pagination-Per-Page, Authorization");
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}
