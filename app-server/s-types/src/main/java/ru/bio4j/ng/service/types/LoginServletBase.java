package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.SecurityHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

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

//    protected void storeCurrentUsrToSession(HttpServletRequest request, User usr) {
//        if(usr != null)
//            request.getSession().setAttribute(User.SESSION_ATTR_NAME, usr);
//        else
//            request.getSession().removeAttribute(User.SESSION_ATTR_NAME);
//    }


    public static String buildLoginParam(HttpServletRequest request) {
        return request.getParameter(BioServletBase.UID_PARAM_NAME);
//        String login = request.getParameter(BioServletBase.LOGIN_PARAM_NAME);
//        if(!Strings.isNullOrEmpty(uid))
//            return uid;
//        else
//            return login;
    }

    protected BioRespBuilder.Login doLogin(HttpServletRequest request) throws Exception {
        BioRespBuilder.Login brsp =  BioRespBuilder.login();
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        String login = buildLoginParam(request);
        brsp.user(securityHandler.getUser(login))
            .success(brsp.getUser() != null);
        return brsp;
    }
}
