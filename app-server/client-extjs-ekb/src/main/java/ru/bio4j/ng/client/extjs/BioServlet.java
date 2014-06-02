package ru.bio4j.ng.client.extjs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Httpc;
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
            final HttpServletResponse rsp = response;
            final String queryString = request.getQueryString();
            final String destination = this.forwardURL+(isNullOrEmpty(queryString) ? "" : "?"+queryString);

            String jsonDataAsQueryParam = request.getParameter(JSON_DATA_PARAM_NAME);
            StringBuilder jd = new StringBuilder();
            if(!isNullOrEmpty(jsonDataAsQueryParam))
                jd.append(jsonDataAsQueryParam);
            else
                Httpc.readDataFromRequest(request, jd);

            Httpc.requestJson(destination, jd.toString(), new Httpc.Callback() {
                @Override
                public void process(InputStream inputStream) throws IOException {
                    OutputStream outputStream = rsp.getOutputStream();
                    try {
                        Httpc.forwardStream(inputStream, outputStream);
                    } finally {
                        if (outputStream != null)
                            outputStream.close();
                    }

                }
            });

        } catch (Exception e) {
            LOG.error("Unexpected error while forwarding!", e);
        }
    }

}
