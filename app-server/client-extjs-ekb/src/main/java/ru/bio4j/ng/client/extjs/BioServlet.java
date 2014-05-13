package ru.bio4j.ng.client.extjs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.api.DataProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class BioServlet extends HttpServlet {
    protected final static Logger LOG = LoggerFactory.getLogger(BioServlet.class);

    protected BioRouter router;

    public BioServlet() {
        router = Utl.getService(this.getServletContext(), BioRouter.class);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpServletRequest rqst = request;
        final HttpServletResponse rspns = response;
        StringBuilder jb = new StringBuilder();
        BufferedReader reader = rqst.getReader();
        String line;
        while ((line = reader.readLine()) != null)
            jb.append(line);
        String requestType = rqst.getParameter("rqt");
        LOG.debug("Recived \"{}\" - request...", requestType);

        try {
            router.route(requestType, jb.toString(), new BioRouter.Callback() {
                @Override
                public void run(String responseBody) throws Exception {
                    rspns.getWriter().append(responseBody);
                }
            });
        } catch (Exception e) {

        }
    }
}
