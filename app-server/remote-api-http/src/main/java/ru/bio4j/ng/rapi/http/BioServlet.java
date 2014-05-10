package ru.bio4j.ng.rapi.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BioServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(BioServlet.class);

    private RemoteAPIService owner;
    public BioServlet(RemoteAPIService owner) {
        this.owner = owner;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
