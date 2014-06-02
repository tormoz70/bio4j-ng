package ru.bio4j.ng.client.extjs.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
        LOG.debug("init - done.");     }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        String servletPath = req.getServletPath();
        HttpSession session = req.getSession();

        LOG.debug("Do filter for sessionId, servletPath, request: {}, {}, {}", session.getId(), servletPath, req);

        chain.doFilter(req, resp);
        if(true) return;


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
        User user = (User) session.getAttribute(User.SESSION_ATTR_NAME);
        if (user != null)
        {
            // User is logged in.
            chain.doFilter(req, resp);
            return;
        }

        // Request is not authorized.
        resp.sendRedirect("login");     }

    @Override
    public void destroy() {
        LOG.debug("Trying destroy");
    }
}
