package ru.bio4j.ng.rapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ayrat on 28.04.14.
 */
public class HelloWorld extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(HelloWorld.class);

    private RemoteAPIService owner;
    public HelloWorld(RemoteAPIService owner) {
        this.owner = owner;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.debug("Request recived (Method:GET)...");
        String userName = null;
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        try {
            userName = this.owner.getDataProvider().getData("tes");
            response.getWriter().append("OK. UserName: " + userName);
            LOG.debug("Request processed (Method:GET). UserName: " + userName);
        } catch (Exception e) {
            response.getWriter().append("Err: " + e.toString());
            LOG.debug("Request processed (Method:GET). Err: " + e.toString());
        }
        response.flushBuffer();
    }
}
