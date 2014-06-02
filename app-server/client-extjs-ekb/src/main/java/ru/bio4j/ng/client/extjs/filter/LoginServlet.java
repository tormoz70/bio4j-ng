package ru.bio4j.ng.client.extjs.filter;

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

public class LoginServlet extends HttpServlet {
    protected final static Logger LOG = LoggerFactory.getLogger(LoginServlet.class);

    protected String forwardURL = null;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException{
        super.init(servletConfig);
        this.forwardURL = servletConfig.getInitParameter("forwardURL");
    }

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

    private final static String CURRENT_USER_ATTR_NAME = "currentUsr";
    private void storeCurrentUsrToSession(HttpServletRequest request, User usr) {
        if(usr != null)
            request.getSession().setAttribute(CURRENT_USER_ATTR_NAME, usr);
        else
            request.getSession().removeAttribute(CURRENT_USER_ATTR_NAME);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpServletRequest req = request;
        initSecurityHandler(this.getServletContext());
        String login = req.getParameter("login");
        String passwd = req.getParameter("passwd");
        login = login + "/" + passwd;
        if(securityHandler != null) {
            try {
                User usr = securityHandler.getUser(login);
                storeCurrentUsrToSession(req, usr);
            } catch (Exception e) {
                LOG.error("Unexpected error while logging in!", e);
            }
        } else {
            final String queryString = req.getQueryString();
            final String destination = this.forwardURL+(isNullOrEmpty(queryString) ? "" : "?"+queryString);
            try {
                Httpc.forwardRequest(destination, req, new Httpc.Callback() {
                    @Override
                    public void process(InputStream inputStream) throws Exception {
                        String jsonUsr = Utl.readStream(inputStream);
                        User usr = Jsons.decode(jsonUsr, User.class);
                        storeCurrentUsrToSession(req, usr);
                    }
                });
            } catch (Exception e) {
                LOG.error("Unexpected error while forwarding login!", e);
            }
        }
    }
}
