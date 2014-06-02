package ru.bio4j.ng.service.types;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.service.api.BioRouter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class BioServletBase extends HttpServlet {

    public static final String FORWARD_URL_PARAM_NAME = "forwardURL";

    protected Logger LOG;

    protected BioRouter router;

    public BioServletBase() {
        LOG = LoggerFactory.getLogger(getClass());
    }

    protected static final String REQUEST_TYPE_PARAM_NAME = "rqt";
    protected static final String JSON_DATA_PARAM_NAME = "jsonData";

    protected void doRoute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            Httpc.readDataFromRequest(request, jd);
        router.route(requestType, jd.toString(), new BioRouter.Callback() {
            @Override
            public void run(String responseBody) throws Exception {
                rspns.getWriter().append(responseBody);
            }
        });
    }

}
