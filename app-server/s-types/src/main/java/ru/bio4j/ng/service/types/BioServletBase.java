package ru.bio4j.ng.service.types;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.api.SecurityHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class BioServletBase extends HttpServlet {

    public final static String LOGIN_PARAM_NAME = "login";
    public final static String UID_PARAM_NAME = "uid";
    public final static String UID_SESSION_ATTR_NAME = "currentUsrUID";
    public final static String FORWARD_URL_PARAM_NAME = "forwardURL";

    protected Logger LOG;

    protected SecurityHandler securityHandler;
    protected BioRouter router;

    public BioServletBase() {
        LOG = LoggerFactory.getLogger(getClass());
    }

    public static void writeResponse(String brespJson, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.append(brespJson);
    }

    protected void initSecurityHandler(ServletContext servletContext) {
        if(securityHandler == null) {
            try {
                securityHandler = Utl.getService(servletContext, SecurityHandler.class);
            } catch (IllegalStateException e) {
                securityHandler = null;
            }
        }
    }

    protected void initRouter(ServletContext servletContext) {
        if(router == null) {
            try {
                router = Utl.getService(servletContext, BioRouter.class);
            } catch (IllegalStateException e) {
                router = null;
            }
        }
    }

    public static void writeResponse(BioResponse bresp, HttpServletResponse response) throws IOException {
//        List<BioError> errs = bresp.getExceptions();
//        BioError e = (errs != null && errs.size() > 0) ? errs.get(0) : null;
//        if(e instanceof BioError.Login.BadLogin) {
//            response.sendError(401);
//            return;
//        }
        String brespJson = Jsons.encode(bresp);
        writeResponse(brespJson, response);
    }

    public static void writeResponse(BioRespBuilder.Builder bresp, HttpServletResponse response) throws IOException {
        writeResponse(bresp.build(), response);
    }

    protected static final String REQUEST_TYPE_PARAM_NAME = "rqt";
    protected static final String JSON_DATA_PARAM_NAME = "jsonData";

    protected void doRoute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(router == null)
            throw new IllegalArgumentException("Router not defined!");
        final HttpServletRequest rqst = request;
        final HttpServletResponse rspns = response;
        final String method = rqst.getMethod();
        final String login = rqst.getParameter(LOGIN_PARAM_NAME);
        final String userUID = rqst.getParameter(UID_PARAM_NAME);
        initSecurityHandler(this.getServletContext());
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final User usr = (!Strings.isNullOrEmpty(login) && login.equals(".")) ? securityHandler.getUser(userUID) : null;


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

        BioRequest bioRequest = Jsons.decode(jd.toString(), BioRoute.getType(requestType).getClazz());

        router.route(bioRequest, new BioRouter.Callback() {
            @Override
            public void run(BioRespBuilder.Builder brsp) throws Exception {
                String responseJson = Jsons.encode(brsp.user(usr).build());
                rspns.getWriter().append(responseJson);
            }
        });
    }

}
