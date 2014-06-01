package ru.bio4j.ng.client.extjs.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.SecurityHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    protected final static Logger LOG = LoggerFactory.getLogger(LoginServlet.class);

    private SecurityHandler securityHandler;
    private void initSecurityHandler(ServletContext servletContext) {
        if(securityHandler == null) {
            try {
                securityHandler = Utl.getService(servletContext, SecurityHandler.class);
            } catch (IllegalStateException e) {
                securityHandler = null;
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initSecurityHandler(this.getServletContext());
        String login = null;
        try {
            User usr = securityHandler.getUser(login);
        } catch (Exception e) {
            LOG.error("Unexpected error while logging in!", e);
        }
    }
}
