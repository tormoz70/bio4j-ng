package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.SecurityHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class LoginServletBase extends HttpServlet {
    protected final static Logger LOG = LoggerFactory.getLogger(LoginServletBase.class);

    protected SecurityHandler securityHandler;
    protected void initSecurityHandler(ServletContext servletContext) {
        if(securityHandler == null) {
            try {
                securityHandler = Utl.getService(servletContext, SecurityHandler.class);
            } catch (IllegalStateException e) {
                securityHandler = null;
            }
        }
    }

    protected void storeCurrentUsrToSession(HttpServletRequest request, User usr) {
        if(usr != null)
            request.getSession().setAttribute(User.SESSION_ATTR_NAME, usr);
        else
            request.getSession().removeAttribute(User.SESSION_ATTR_NAME);
    }

    protected User doLogin(HttpServletRequest request) throws Exception {
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        String login = request.getParameter("login");
        return securityHandler.getUser(login);
    }
}
