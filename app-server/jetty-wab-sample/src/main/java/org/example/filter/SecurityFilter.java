package org.example.filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author vbochenin
 * @since 12/03/2014.
 */
public class SecurityFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(SecurityFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("Do filter for request: {}", request);

        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {

        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.debug("Trying destroy");
    }
}
