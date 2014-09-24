package ru.bio4j.ng.service.types;


import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.BioError;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class WarServletApiBase extends BioServletApiBase {

    protected String forwardURL = null;

    /**
     * This method called by servlet container
     * @param servletConfig
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(ServletConfig servletConfig) throws ServletException{
        super.init(servletConfig);
        bioDebug = Strings.compare(servletConfig.getInitParameter(BioServletBase.BIODEBUG_PARAM_NAME), "true", true);
        forwardURL = servletConfig.getInitParameter(BioServletBase.FORWARD_URL_PARAM_NAME);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            initRouter(this.getServletContext());
            if(router != null) {
                LOG.debug("Router detected! This is WAB-mode! Lets route request!");
                initServices(this.getServletContext());
                doRoute(request, response);
            } else {
                LOG.debug("Router NOT detected! This is WAR-mode! Lets forward request!");
                doFwd(request, response);
            }
        } catch (Exception e) {
            LOG.error("Unexpected server error (Level-1)!", e);
            responseError(BioError.wrap(e), response);
        }
    }

    private void doFwd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final HttpServletResponse rsp = response;
        rsp.setCharacterEncoding("UTF-8");
        final String queryString = Httpc.getQueryString(request);
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
    }

}
