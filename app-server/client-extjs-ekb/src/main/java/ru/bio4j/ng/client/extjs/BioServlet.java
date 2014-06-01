package ru.bio4j.ng.client.extjs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.api.BioServletBase;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class BioServlet extends BioServletBase {

    protected String forwardURL = null;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException{
        super.init(servletConfig);
        this.forwardURL = servletConfig.getInitParameter("forwardURL");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private void initRouter(ServletContext servletContext) {
        if(router == null) {
            try {
                router = Utl.getService(servletContext, BioRouter.class);
            } catch (IllegalStateException e) {
                router = null;
            }
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        initRouter(this.getServletContext());
        if(router != null) {
            LOG.debug("Router detected! This is WAB-mode! Lets route request!");
            doRoute(request, response);
        } else {
            LOG.debug("Router NOT detected! This is WAR-mode! Lets forward request!");
            doFwd(request, response);
        }
    }

    private void doFwd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            final String queryString = request.getQueryString();
            final String method = request.getMethod();
            final String destinationServer = this.forwardURL+(isNullOrEmpty(queryString) ? "" : "?"+queryString);
            final String destination = destinationServer; //request.getParameter("p");

            URL url = new URL(destination);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if(method.equals("POST")) {
                StringBuilder jsonData = new StringBuilder();
                readDataFromRequest(request, jsonData);
                byte[] data = jsonData.toString().getBytes();
                connection.setRequestProperty("Content-Length", Integer.toString(data.length));
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(jsonData.toString());
                wr.flush();
                wr.close();
            }
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                OutputStream out = response.getOutputStream();
                try {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1)
                        out.write(buffer, 0, bytesRead);
                    is.close();
                    out.close();
                } finally {
                    if (is != null)
                        is.close();
                    if (out != null)
                        out.close();
                }
            } else {
                response.getWriter().println(String.format("Error on forwarded server: [%d] - %s", connection.getResponseCode(), connection.getResponseMessage()));
            }
        } catch (Exception e) {
            LOG.error("Unexpected error while forwarding! Error: {}", e.toString());
        }
    }

}
