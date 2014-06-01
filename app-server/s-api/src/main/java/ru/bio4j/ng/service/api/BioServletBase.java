package ru.bio4j.ng.service.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class BioServletBase extends HttpServlet {
    protected Logger LOG;

    protected BioRouter router;

    public BioServletBase() {
        LOG = LoggerFactory.getLogger(getClass());
    }

    private static final String REQUEST_TYPE_PARAM_NAME = "rqt";
    private static final String JSON_DATA_PARAM_NAME = "jsonData";

    protected static void readDataFromRequest(HttpServletRequest request, StringBuilder jd) throws IOException {
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null)
            jd.append(line);
    }

    protected void doRoute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if(router == null)
                throw new IllegalArgumentException("Router not defined!");
            final HttpServletRequest rqst = request;
            final HttpServletResponse rspns = response;
            final String method = rqst.getMethod();
            final String requestType = rqst.getParameter(REQUEST_TYPE_PARAM_NAME);
            LOG.debug("Recived-{}: \"{}\" - request...", method, requestType);
            if(isNullOrEmpty(requestType))
                throw new IllegalArgumentException(String.format("Parameter \"%s\" cannot be null or empty!", REQUEST_TYPE_PARAM_NAME));

            String jsonDataAsQueryParam = rqst.getParameter(JSON_DATA_PARAM_NAME);
            StringBuilder jd = new StringBuilder();
            if(!isNullOrEmpty(jsonDataAsQueryParam))
                jd.append(jsonDataAsQueryParam);
            else
                readDataFromRequest(request, jd);
            router.route(requestType, jd.toString(), new BioRouter.Callback() {
                @Override
                public void run(String responseBody) throws Exception {
                    rspns.getWriter().append(responseBody);
                }
            });
        } catch (Exception e) {
            LOG.error("Unexpected error while routing!", e);
        }
    }

}
