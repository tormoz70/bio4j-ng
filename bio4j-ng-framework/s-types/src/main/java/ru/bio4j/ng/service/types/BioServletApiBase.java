package ru.bio4j.ng.service.types;


import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class BioServletApiBase extends BioServletBase {

    protected BioRouter router;
    //protected ModuleProvider moduleProvider;

    protected void initRouter(ServletContext servletContext) {
        if(router == null) {
            try {
                router = Utl.getService(servletContext, BioRouter.class);
            } catch (IllegalStateException e) {
                router = null;
            }
        }
    }

    @Override
    protected void initServices(ServletContext servletContext) throws Exception {
        super.initServices(servletContext);

//        if(moduleProvider == null) {
//            try {
//                moduleProvider = Utl.getService(servletContext, ModuleProvider.class);
//            } catch (IllegalStateException e) {
//                moduleProvider = null;
//            }
//        }
//        if(moduleProvider == null)
//            throw new IllegalArgumentException("ModuleProvider not defined!");
    }

//    protected static final String QRY_PARAM_NAME_REQUEST_TYPE = "rqt";

    protected void doRoute(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if(router == null)
            throw new IllegalArgumentException("Router not defined!");
//        final String method = request.getMethod();
//        final String moduleKey = request.getParameter(QRY_PARAM_NAME_MODULE);
//        final String userUID = request.getParameter(QRY_PARAM_NAME_UID);
//
//        final User usr = securityHandler.getUser(moduleKey, userUID);
//
//        final String requestType = request.getParameter(QRY_PARAM_NAME_REQUEST_TYPE);
//        LOG.debug("Recived-{}: \"{}\" - request...", method, requestType);
//        if(isNullOrEmpty(requestType))
//            throw new IllegalArgumentException(String.format("Parameter \"%s\" cannot be null or empty!", QRY_PARAM_NAME_REQUEST_TYPE));

//        BioModule bioModule = moduleProvider.getModule(moduleKey);
//        if(bioModule == null)
//            throw new IllegalArgumentException(String.format("Module with key \"%s\" not registered!", moduleKey));
//        BioHttpRequestProcessor requestProcessor = bioModule.getHttpRequestProcessor(requestType);
//        if(requestProcessor != null){
//            requestProcessor.doPost(request, response);
//            return;
//        }

//        BioRequest bioRequest = null;
//        try {
//            BioRoute route = BioRoute.getType(requestType);
//            if(route == null)
//                throw new Exception(String.format("Route for requestType \"%s\" not found!", requestType));
//            BioRequestFactory factory = route.getFactory();
//            bioRequest = factory.restore(request, moduleKey, route, usr);
//        } catch (Exception e) {
//            LOG.debug("Unexpected error while decoding BioRequest: \n"+
//                    " - Error: {}", e.getMessage());
//            throw e;
//        }

        router.route(request, response);
    }

}
