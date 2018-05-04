package ru.bio4j.ng.router;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreExpDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetRecord;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStorePost;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;
import static ru.bio4j.ng.service.api.BioRoute.*;

@Component
@Instantiate
@Provides(specifications = BioRouter.class)
public class BioRouterImpl extends BioServiceBase implements BioRouter {
    private static final Logger LOG = LoggerFactory.getLogger(BioRouterImpl.class);

    private Map<String, BioRouteHandler> routeMap;
    private Map<BioRoute, Class<? extends BioRequestFactory>> factoryMap;

    @Requires
    private SecurityProvider securityProvider;
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
        if(factoryMap == null) {
            factoryMap = new HashMap<>();
            factoryMap.put(PING, BioRequestFactory.Ping.class);
            factoryMap.put(LOGIN, BioRequestFactory.Login.class);
            factoryMap.put(LOGOUT, BioRequestFactory.Logout.class);
            factoryMap.put(CRUD_JSON_GET, BioRequestFactory.GetJson.class);
            factoryMap.put(CRUD_FILE_GET, BioRequestFactory.GetFile.class);
            factoryMap.put(CRUD_DATASET_GET, BioRequestFactory.GetDataSet.class);
            factoryMap.put(CRUD_DATASET_EXP, BioRequestFactory.ExpDataSet.class);
            factoryMap.put(CRUD_RECORD_GET, BioRequestFactory.GetRecord.class);
            factoryMap.put(CRUD_DATASET_POST, BioRequestFactory.DataSetPost.class);
            factoryMap.put(CRUD_EXEC, BioRequestFactory.StoredProg.class);
            factoryMap.put(CRUD_FCLOUD, BioRequestFactory.FCloud.class);
        }

        if(routeMap == null) {
            routeMap = new HashMap<>();

            routeMap.put(BioRoute.UNKNOWN.getAlias(), new BioRouteHandler<BioRequest>() {
                @Override
                public boolean handle(BioRequest request, HttpServletResponse response) throws Exception {
                    BioRespBuilder.Builder brsp = BioRespBuilder.anErrorBuilder()
                            .exception(new BioError.BadRequestType(request.getRequestType()));
                    response.getWriter().append(brsp.json());
                    return true;
                }
            });

            routeMap.put(PING.getAlias(), new BioRouteHandler<BioRequest>() {
                @Override
                public boolean handle(BioRequest request, HttpServletResponse response) throws Exception {
                    BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().user(request.getUser()).exception(null);
                    response.getWriter().append(responseBuilder.json());
                    return true;
                }
            });

            routeMap.put(LOGIN.getAlias(), new BioRouteHandler<BioRequest>() {
                @Override
                public boolean handle(BioRequest request, HttpServletResponse response) throws Exception {
                    throw new UnsupportedOperationException("This method should not be called here!");
                }
            });

            routeMap.put(LOGOUT.getAlias(), new BioRouteHandler<BioRequest>() {
                @Override
                public boolean handle(BioRequest request, HttpServletResponse response) throws Exception {
                    throw new UnsupportedOperationException("This method should not be called here!");
                }
            });

            routeMap.put(CRUD_JSON_GET.getAlias(), new BioRouteHandler<BioRequestGetJson>() {
                @Override
                public boolean handle(final BioRequestGetJson request, final HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", CRUD_JSON_GET);
                    dataProvider.processRequest(CRUD_JSON_GET, request, response);
                    return true;
                }
            });

            routeMap.put(BioRoute.CRUD_FILE_GET.getAlias(), new BioRouteHandler<BioRequestGetFile>() {
                @Override
                public boolean handle(final BioRequestGetFile request, final HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_FILE_GET);
                    dataProvider.processRequest(BioRoute.CRUD_FILE_GET, request, response);
                    return true;
                }
            });

            routeMap.put(BioRoute.CRUD_DATASET_GET.getAlias(), new BioRouteHandler<BioRequestJStoreGetDataSet>() {
                @Override
                public boolean handle(BioRequestJStoreGetDataSet request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_DATASET_GET);
                    dataProvider.processRequest(BioRoute.CRUD_DATASET_GET, request, response);
                    return true;
                }
            });

            routeMap.put(BioRoute.CRUD_DATASET_EXP.getAlias(), new BioRouteHandler<BioRequestJStoreExpDataSet>() {
                @Override
                public boolean handle(BioRequestJStoreExpDataSet request, HttpServletResponse response) throws Exception {
                    LOG.debug("Request {} not implemented.", BioRoute.CRUD_DATASET_EXP);
                    return true;
                }
            });

            routeMap.put(BioRoute.CRUD_RECORD_GET.getAlias(), new BioRouteHandler<BioRequestJStoreGetRecord>() {
                @Override
                public boolean handle(BioRequestJStoreGetRecord request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_RECORD_GET);
                    dataProvider.processRequest(BioRoute.CRUD_RECORD_GET, request, response);
                    return true;
                }
            });

            routeMap.put(BioRoute.CRUD_DATASET_POST.getAlias(), new BioRouteHandler<BioRequestJStorePost>() {
                @Override
                public boolean handle(BioRequestJStorePost request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_DATASET_POST);
                    dataProvider.processRequest(BioRoute.CRUD_DATASET_POST, request, response);
                    return true;
                }
            });

            routeMap.put(BioRoute.CRUD_EXEC.getAlias(), new BioRouteHandler<BioRequestStoredProg>() {
                @Override
                public boolean handle(BioRequestStoredProg request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_EXEC);
                    dataProvider.processRequest(BioRoute.CRUD_EXEC, request, response);
                    return true;
                }
            });

            routeMap.put(BioRoute.CRUD_FCLOUD.getAlias(), new BioRouteHandler<BioRequestFCloud>() {
                @Override
                public boolean handle(BioRequestFCloud request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_FCLOUD);
                    dataProvider.processRequest(BioRoute.CRUD_FCLOUD, request, response);
                    return true;
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

        final BioQueryParams qprms = ((BioWrappedRequest) request).getBioQueryParams();

//        final User usr = securityProvider.getUser(qprms.stoken, qprms.remoteIP);
        final User usr = ((BioWrappedRequest) request).getUser();
        if(usr == null)
            throw new Exception("Something wrong! Var \"usr\" cannot be null in this way!");

        final String requestType = qprms.requestType;
        LOG.debug("Recived-{}: \"{}\" - request...", qprms.method, requestType);
        if(isNullOrEmpty(requestType))
            throw new IllegalArgumentException(String.format("Parameter \"requestType\" cannot be null or empty!"));

        BioAppModule bioModule = moduleProvider.getAppModule(qprms.moduleKey);
        if(bioModule == null)
            throw new IllegalArgumentException(String.format("Module with key \"%s\" not registered!", qprms.moduleKey));
        BioHttpRequestProcessor requestProcessor = bioModule.getHttpRequestProcessor(requestType);

        if(requestProcessor != null){
            //Do processing with BioHttpRequestProcessor
            requestProcessor.doPost(request, response);
        } else {
            //Do processing with BioRoute

            BioRoute route = BioRoute.getType(requestType);
            if (route == null)
                throw new Exception(String.format("Route for requestType \"%s\" not found!", requestType));

            BioRequest bioRequest;
            try {
                Class<? extends BioRequestFactory> factoryClazz = factoryMap.get(route);
                BioRequestFactory factory = factoryClazz.newInstance();
                bioRequest = factory.restore(qprms, route.getClazz(), usr);
            } catch (Exception e) {
                LOG.debug("Unexpected error while decoding BioRequest: \n" +
                        " - Error: {}", e.getMessage());
                throw e;
            }

            boolean requestHandled = false;
            BioRouteHandler routeHandler = bioModule.getRouteHandler(route.getAlias());
            if(routeHandler != null)
                requestHandled = routeHandler.handle(bioRequest, response);
            if(!requestHandled) {
                routeHandler = routeMap.get(route.getAlias());
                if (routeHandler == null)
                    throw new Exception(String.format("RouteHandler for requestType \"%s\" not found!", route.getAlias()));
                try {
                    routeHandler.handle(bioRequest, response);
                } catch (Exception e) {
                    String msg = String.format("Unexpected error while handling BioRequest(%s): \n" +
                            " - Error: %s", bioRequest.getBioCode(), e.getMessage());
                    LOG.debug(msg);
                    throw new Exception(msg, e);
                }
            }

        }
    }
}
