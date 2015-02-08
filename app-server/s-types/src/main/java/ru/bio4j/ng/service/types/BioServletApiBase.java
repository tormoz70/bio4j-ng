package ru.bio4j.ng.service.types;


import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.BioRouter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class BioServletApiBase extends BioServletBase {

    protected BioRouter router;

    protected void initRouter(ServletContext servletContext) {
        if(router == null) {
            try {
                router = Utl.getService(servletContext, BioRouter.class);
            } catch (IllegalStateException e) {
                router = null;
            }
        }
    }

    protected static final String QRY_PARAM_NAME_REQUEST_TYPE = "rqt";
    protected static final String QRY_PARAM_NAME_JSON_DATA = "jsonData";


    protected void doRoute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(router == null)
            throw new IllegalArgumentException("Router not defined!");
        final HttpServletRequest rqst = request;
        final HttpServletResponse rspns = response;
        final String method = rqst.getMethod();
        final String moduleKey = rqst.getParameter(QRY_PARAM_NAME_MODULE);
        final String userUID = rqst.getParameter(QRY_PARAM_NAME_UID);

        final User usr = securityHandler.getUser(moduleKey, userUID);

        final String requestType = rqst.getParameter(QRY_PARAM_NAME_REQUEST_TYPE);
        LOG.debug("Recived-{}: \"{}\" - request...", method, requestType);
        if(isNullOrEmpty(requestType))
            throw new IllegalArgumentException(String.format("Parameter \"%s\" cannot be null or empty!", QRY_PARAM_NAME_REQUEST_TYPE));

        String jsonDataAsQueryParam = rqst.getParameter(QRY_PARAM_NAME_JSON_DATA);
        StringBuilder jd = new StringBuilder();
        if(!isNullOrEmpty(jsonDataAsQueryParam))
            jd.append(jsonDataAsQueryParam);
        else
            Httpc.readDataFromRequest(request, jd);
        if(jd.length() == 0)
            jd.append("{}");
        BioRequest bioRequest;
        String bioRequestJson = jd.toString();
        try {
            Class<? extends BioRequest> clazz = BioRoute.getType(requestType).getClazz();
            if(clazz == null)
                throw new Exception(String.format("Clazz for requestType \"%s\" not found!", requestType));
            bioRequest = Jsons.decode(bioRequestJson, clazz);
        } catch (Exception e) {
            LOG.debug("Unexpected error while decoding BioRequest JSON: {}\n"+
                    " - Error: {}", bioRequestJson, e.getMessage());
            throw e;
        }
        bioRequest.setModuleKey(moduleKey);
        bioRequest.setRequestType(requestType);
        bioRequest.setUser(usr);

        router.route(bioRequest, new BioRouter.Callback() {
            @Override
            public void run(BioRespBuilder.Builder brsp) throws Exception {
            String responseJson = Jsons.encode(brsp.user(usr).build());
            rspns.getWriter().append(responseJson);
            }
        });
    }

}
