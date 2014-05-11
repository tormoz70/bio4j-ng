package ru.bio4j.ng.rapi.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

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
            userName = this.owner.getDataProvider().getDataTest();
            response.getWriter().append("OK. UserName: " + userName);
            LOG.debug("Request processed (Method:GET). UserName: " + userName);

//            LOG.debug("Sending event...");
//            owner.getEventAdmin().sendEvent(new Event("ehcache-updated", new HashMap<String, Object>()));
//            LOG.debug("Event sent.");

        } catch (Exception e) {
            response.getWriter().append("Err: " + e.toString());
            LOG.debug("Request processed (Method:GET). Err: " + e.toString());
        }
        response.flushBuffer();
    }
}
