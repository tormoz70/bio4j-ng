package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.SecurityHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

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

    protected void writeResponse(String brespJson, PrintWriter writer) throws IOException {
        writer.append(brespJson);
    }

    protected void writeResponse(BioRespBuilder.Builder bresp, PrintWriter writer) throws IOException {
        String brespJson = Jsons.encode(bresp.build());
        writeResponse(brespJson, writer);
    }

    protected BioRespBuilder.Login doLogin(HttpServletRequest request) throws Exception {
        BioRespBuilder.Login brsp =  BioRespBuilder.create(BioRespBuilder.Login.class);
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        String login = request.getParameter("login");
        try {
            brsp.user(securityHandler.getUser(login))
                .success(brsp.getUser() != null);
        } catch (Exception e) {
            brsp.success(false)
                .addError(e);
        }
        return brsp;
    }
}
