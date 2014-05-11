package org.example.filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author vbochenin
 * @since 12/03/2014.
 */
public class SecurityFilter implements Filter {

    private final static Logger LOG = LoggerFactory.getLogger(SecurityFilter.class);

    private String errorPage;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.debug("init...");
        if (filterConfig != null) {
            errorPage = filterConfig.getInitParameter("error_page");
            LOG.debug("errorPage - {}.", errorPage);
        }
        LOG.debug("init - done.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.debug("Do filter for request: {}", request);

        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        String servletPath = req.getServletPath();

        // Allow access to login functionality.
        if (servletPath.equals("/login"))
        {
            chain.doFilter(req, resp);
            return;
        }
        // Allow access to news feed.
        if (servletPath.equals("/news.rss")) {
            chain.doFilter(req, resp);
            return;
        }
        // All other functionality requires authentication.
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null)
        {
            // User is logged in.
            chain.doFilter(req, resp);
            return;
        }

        // Request is not authorized.
        resp.sendRedirect("login");
    }

    @Override
    public void destroy() {
        LOG.debug("Trying destroy");
    }
}
