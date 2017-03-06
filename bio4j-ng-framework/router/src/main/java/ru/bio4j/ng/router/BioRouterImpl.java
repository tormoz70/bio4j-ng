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
import ru.bio4j.ng.service.types.BioServiceBase;
import ru.bio4j.ng.service.api.SrvcUtils;
import ru.bio4j.ng.service.types.BioWrappedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

@Component
@Instantiate
@Provides(specifications = BioRouter.class)
public class BioRouterImpl extends BioServiceBase implements BioRouter {
    private static final Logger LOG = LoggerFactory.getLogger(BioRouterImpl.class);

    private Map<String, BioRouteHandler> routeMap;

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

        if(routeMap == null) {
            routeMap = new HashMap<>();

            routeMap.put(BioRoute.UNKNOWN.getAlias(), new BioRouteHandler<BioRequest>() {
                @Override
                public void handle(BioRequest request, HttpServletResponse response) throws Exception {
                    BioRespBuilder.Builder brsp = BioRespBuilder.anErrorBuilder()
                            .exception(new BioError.BadRequestType(request.getRequestType()));
                    response.getWriter().append(brsp.json());

                }
            });

            routeMap.put(BioRoute.PING.getAlias(), new BioRouteHandler<BioRequest>() {
                @Override
                public void handle(BioRequest request, HttpServletResponse response) throws Exception {
                    BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().user(request.getUser()).exception(null);
                    response.getWriter().append(responseBuilder.json());
                }
            });

            routeMap.put(BioRoute.LOGIN.getAlias(), new BioRouteHandler<BioRequest>() {
                @Override
                public void handle(BioRequest request, HttpServletResponse response) throws Exception {
                    throw new UnsupportedOperationException("This method should not be called here!");
                }
            });

            routeMap.put(BioRoute.LOGOUT.getAlias(), new BioRouteHandler<BioRequest>() {
                @Override
                public void handle(BioRequest request, HttpServletResponse response) throws Exception {
                    throw new UnsupportedOperationException("This method should not be called here!");
                }
            });

            routeMap.put(BioRoute.CRUD_JSON_GET.getAlias(), new BioRouteHandler<BioRequestGetJson>() {
                @Override
                public void handle(final BioRequestGetJson request, final HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_JSON_GET);
                    dataProvider.processRequest(BioRoute.CRUD_JSON_GET, request, response);
                }
            });

            routeMap.put(BioRoute.CRUD_FILE_GET.getAlias(), new BioRouteHandler<BioRequestGetFile>() {
                @Override
                public void handle(final BioRequestGetFile request, final HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_FILE_GET);
                    dataProvider.processRequest(BioRoute.CRUD_FILE_GET, request, response);

                }
            });

            routeMap.put(BioRoute.CRUD_DATASET_GET.getAlias(), new BioRouteHandler<BioRequestJStoreGetDataSet>() {
                @Override
                public void handle(BioRequestJStoreGetDataSet request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_DATASET_GET);
                    dataProvider.processRequest(BioRoute.CRUD_DATASET_GET, request, response);
                }
            });

            routeMap.put(BioRoute.CRUD_DATASET_EXP.getAlias(), new BioRouteHandler<BioRequestJStoreExpDataSet>() {
                @Override
                public void handle(BioRequestJStoreExpDataSet request, HttpServletResponse response) throws Exception {
                    LOG.debug("Request {} not implemented.", BioRoute.CRUD_DATASET_EXP);
                }
            });

            routeMap.put(BioRoute.CRUD_RECORD_GET.getAlias(), new BioRouteHandler<BioRequestJStoreGetRecord>() {
                @Override
                public void handle(BioRequestJStoreGetRecord request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_RECORD_GET);
                    dataProvider.processRequest(BioRoute.CRUD_RECORD_GET, request, response);
                }
            });

            routeMap.put(BioRoute.CRUD_DATASET_POST.getAlias(), new BioRouteHandler<BioRequestJStorePost>() {
                @Override
                public void handle(BioRequestJStorePost request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_DATASET_POST);
                    dataProvider.processRequest(BioRoute.CRUD_DATASET_POST, request, response);
                }
            });

            routeMap.put(BioRoute.CRUD_EXEC.getAlias(), new BioRouteHandler<BioRequestStoredProg>() {
                @Override
                public void handle(BioRequestStoredProg request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_EXEC);
                    dataProvider.processRequest(BioRoute.CRUD_EXEC, request, response);
                }
            });

            routeMap.put(BioRoute.CRUD_FORM_UPLOAD.getAlias(), new BioRouteHandler<BioRequestUpload>() {
                @Override
                public void handle(BioRequestUpload request, HttpServletResponse response) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_FORM_UPLOAD);
                    dataProvider.processRequest(BioRoute.CRUD_FORM_UPLOAD, request, response);
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

        final SrvcUtils.BioQueryParams qprms = ((BioWrappedRequest) request).getBioQueryParams();

        final User usr = securityProvider.getUser(qprms.stoken, qprms.remoteIP);
        if(usr == null)
            throw new Exception("Something wrong! Var \"usr\" cannot be null in this way!");

        final String requestType = qprms.requestType;
        LOG.debug("Recived-{}: \"{}\" - request...", qprms.method, requestType);
        if(isNullOrEmpty(requestType))
            throw new IllegalArgumentException(String.format("Parameter \"%s\" cannot be null or empty!", SrvcUtils.QRY_PARAM_NAME_REQUEST_TYPE));

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
                BioRequestFactory factory = route.getFactory();
                bioRequest = factory.restore(qprms, route.getClazz(), usr);
            } catch (Exception e) {
                LOG.debug("Unexpected error while decoding BioRequest: \n" +
                        " - Error: {}", e.getMessage());
                throw e;
            }

            BioRouteHandler routeHandler = bioModule.getRouteHandler(route.getAlias());
            if(routeHandler == null)
                routeHandler = routeMap.get(route.getAlias());
            if(routeHandler == null)
                throw new Exception(String.format("RouteHandler for requestType \"%s\" not found!", route.getAlias()));

            routeHandler.handle(bioRequest, response);
        }
    }
}
