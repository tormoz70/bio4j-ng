package ru.bio4j.ng.router;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetRecord;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStorePost;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;
import ru.bio4j.ng.service.types.SrvcUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

@Component
@Instantiate
@Provides(specifications = BioRouter.class)
public class BioRouterImpl extends BioServiceBase implements BioRouter {
    private static final Logger LOG = LoggerFactory.getLogger(BioRouterImpl.class);

    private Map<BioRoute, BioRouteHandler> routeMap;

    @Requires
    private SecurityHandler securityHandler;
    @Requires
    private DataProvider dataProvider;
    @Requires
    protected ModuleProvider moduleProvider;

//    private static void  processCallback(BioRespBuilder.Builder brsp, Callback callback) throws Exception {
//        if(callback != null) {
//            //String responseJson = Jsons.encode(brsp.build());
//            callback.run(brsp);
//        }
//    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");

        if(routeMap == null) {
            routeMap = new HashMap<>();

            routeMap.put(BioRoute.UNKNOWN, new BioRouteHandler<BioRequest>() {
                @Override
                public void handle(BioRequest request, HttpServletResponse response) throws Exception {
                    BioRespBuilder.Builder brsp = BioRespBuilder.anErrorBuilder()
                            .exception(new BioError.BadRequestType(request.getRequestType())).user(request.getUser());
                    response.getWriter().append(brsp.json());

                }
            });

            routeMap.put(BioRoute.PING, new BioRouteHandler<BioRequest>() {
                @Override
                public void handle(BioRequest request, HttpServletResponse response) throws Exception {
                    BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().user(request.getUser());
                    response.getWriter().append(responseBuilder.json());
                }
            });

            routeMap.put(BioRoute.LOGOUT, new BioRouteHandler<BioRequest>() {
                @Override
                public void handle(BioRequest request, HttpServletResponse response) throws Exception {
                    final String moduleKey = request.getModuleKey();
                    final String userUID = request.getUser().getUid();
                    securityHandler.logoff(moduleKey, userUID);
                    BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().user(request.getUser());
                    response.getWriter().append(responseBuilder.json());
                }
            });

            routeMap.put(BioRoute.CRUD_JSON_GET, new BioRouteHandler<BioRequestGetJson>() {
                @Override
                public void handle(final BioRequestGetJson request, final HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_JSON_GET);
                    BioRespBuilder.Builder responseBuilder = dataProvider.processRequest(BioRoute.CRUD_JSON_GET, request).user(request.getUser());
                    response.getWriter().append(responseBuilder.json());
                }
            });

            routeMap.put(BioRoute.CRUD_DATASET_GET, new BioRouteHandler<BioRequestJStoreGetDataSet>() {
                @Override
                public void handle(BioRequestJStoreGetDataSet request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_DATASET_GET);
                    BioRespBuilder.Builder responseBuilder = dataProvider.processRequest(BioRoute.CRUD_DATASET_GET, request).user(request.getUser());
                    response.getWriter().append(responseBuilder.json());
                }
            });

            routeMap.put(BioRoute.CRUD_RECORD_GET, new BioRouteHandler<BioRequestJStoreGetRecord>() {
                @Override
                public void handle(BioRequestJStoreGetRecord request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_RECORD_GET);
                    BioRespBuilder.Builder responseBuilder = dataProvider.processRequest(BioRoute.CRUD_RECORD_GET, request).user(request.getUser());
                    response.getWriter().append(responseBuilder.json());
                }
            });

            routeMap.put(BioRoute.CRUD_DATASET_POST, new BioRouteHandler<BioRequestJStorePost>() {
                @Override
                public void handle(BioRequestJStorePost request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_DATASET_POST);
                    BioRespBuilder.Builder responseBuilder = dataProvider.processRequest(BioRoute.CRUD_DATASET_POST, request).user(request.getUser());
                    response.getWriter().append(responseBuilder.json());
                }
            });

            routeMap.put(BioRoute.CRUD_EXEC, new BioRouteHandler<BioRequestStoredProg>() {
                @Override
                public void handle(BioRequestStoredProg request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_EXEC);
                    BioRespBuilder.Builder responseBuilder = dataProvider.processRequest(BioRoute.CRUD_EXEC, request).user(request.getUser());
                    response.getWriter().append(responseBuilder.json());
                }
            });

            routeMap.put(BioRoute.CRUD_FORM_UPLOAD, new BioRouteHandler<BioRequestJStorePost>() {
                @Override
                public void handle(BioRequestJStorePost request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_FORM_UPLOAD);
                    BioRespBuilder.Builder responseBuilder = dataProvider.processRequest(BioRoute.CRUD_FORM_UPLOAD, request).user(request.getUser());
                    response.getWriter().append(responseBuilder.json());
                }
            });

        }

        this.ready = true;
        LOG.debug("Started");
    }

    @Invalidate
    public void doStop() throws Exception {
        LOG.debug("Stoping...");
        this.ready = false;
        LOG.debug("Stoped.");
    }

    @Subscriber(
            name="router.subscriber",
            topics="bio-config-updated")
    public void receive(Event e) throws Exception {
        LOG.debug("Config updated event recived!!!");
        doStop();
        doStart();
    }

    @Override
    public void route(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final String method = request.getMethod();
        final String moduleKey = request.getParameter(SrvcUtils.QRY_PARAM_NAME_MODULE);
        final String userUID = request.getParameter(SrvcUtils.QRY_PARAM_NAME_UID);

        final User usr = securityHandler.getUser(moduleKey, userUID);

        final String requestType = request.getParameter(SrvcUtils.QRY_PARAM_NAME_REQUEST_TYPE);
        LOG.debug("Recived-{}: \"{}\" - request...", method, requestType);
        if(isNullOrEmpty(requestType))
            throw new IllegalArgumentException(String.format("Parameter \"%s\" cannot be null or empty!", SrvcUtils.QRY_PARAM_NAME_REQUEST_TYPE));

        BioModule bioModule = moduleProvider.getModule(moduleKey);
        if(bioModule == null)
            throw new IllegalArgumentException(String.format("Module with key \"%s\" not registered!", moduleKey));
        BioHttpRequestProcessor requestProcessor = bioModule.getHttpRequestProcessor(requestType);
        if(requestProcessor != null){
            requestProcessor.doPost(request, response);
            return;
        }

        BioRequest bioRequest = null;
        try {
            BioRoute route = BioRoute.getType(requestType);
            if(route == null)
                throw new Exception(String.format("Route for requestType \"%s\" not found!", requestType));
            BioRequestFactory factory = route.getFactory();
            bioRequest = factory.restore(request, moduleKey, route, usr);
        } catch (Exception e) {
            LOG.debug("Unexpected error while decoding BioRequest: \n"+
                    " - Error: {}", e.getMessage());
            throw e;
        }

        BioRoute type = BioRoute.getType(bioRequest.getRequestType());
        routeMap.get(type).handle(bioRequest, response);

    }
}
