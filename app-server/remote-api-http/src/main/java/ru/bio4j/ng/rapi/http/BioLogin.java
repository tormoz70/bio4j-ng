package ru.bio4j.ng.rapi.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.SecurityHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ayrat on 01.06.14.
 */
public class BioLogin extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(BioLogin.class);

    private final SecurityHandler securityHandler;

    public BioLogin(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpServletRequest req = request;
        final HttpServletResponse resp = response;
        String login = req.getParameter("login");
        String passwd = req.getParameter("passwd");
        try {
            User usr = securityHandler.getUser(login + "/" + passwd);
            if(usr != null){
                String jsonData = Jsons.encode(usr);
                resp.getWriter().append(jsonData);
            }
        } catch (Exception e) {
            LOG.error("Unexpected error while logging in!", e);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

}
