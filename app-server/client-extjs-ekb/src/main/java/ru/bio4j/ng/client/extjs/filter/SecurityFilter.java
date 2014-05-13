package ru.bio4j.ng.client.extjs.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

public class SecurityFilter implements Filter {

    private final static Logger LOG = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.debug("Do filter for request: {}", request);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOG.debug("Trying destroy");
    }
}
