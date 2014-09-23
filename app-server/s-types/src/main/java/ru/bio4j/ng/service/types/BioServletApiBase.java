package ru.bio4j.ng.service.types;


import flexjson.ObjectFactory;
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

    protected static final String REQUEST_TYPE_PARAM_NAME = "rqt";
    protected static final String JSON_DATA_PARAM_NAME = "jsonData";


    protected void doRoute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(router == null)
            throw new IllegalArgumentException("Router not defined!");
        final HttpServletRequest rqst = request;
        final HttpServletResponse rspns = response;
        final String method = rqst.getMethod();
        final String moduleKey = rqst.getParameter(MODULE_PARAM_NAME);
        final String userUID = rqst.getParameter(UID_PARAM_NAME);

        final User usr = securityHandler.getUser(moduleKey, userUID);

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
        if(jd.length() == 0)
            jd.append("{}");
        BioRequest bioRequest;
        String bioRequestJson = jd.toString();
        try {
//            Class<? extends BioRequestFactory> factory = BioRoute.getType(requestType).getFactory();
//            if(factory == null)
//                throw new Exception(String.format("BioRequestFactory for requestType \"%s\" not found!", requestType));
//            bioRequest = Jsons.decode(bioRequestJson, factory.newInstance());
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
