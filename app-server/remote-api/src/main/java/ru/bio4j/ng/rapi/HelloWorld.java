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
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.debug("Request recived (Method:GET)...");
        response.getWriter().append("OK");
        response.flushBuffer();
        LOG.debug("Request processed (Method:GET).");
    }
}
