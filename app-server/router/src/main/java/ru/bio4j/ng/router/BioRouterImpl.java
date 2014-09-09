package ru.bio4j.ng.router;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetRecord;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStorePost;
import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.api.DataProvider;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.types.BioRoute;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.util.HashMap;
import java.util.Map;

@Component
@Instantiate
@Provides(specifications = BioRouter.class)
public class BioRouterImpl extends BioServiceBase implements BioRouter {
    private static final Logger LOG = LoggerFactory.getLogger(BioRouterImpl.class);

    private Map<BioRoute, BioRouteHandler> routeMap;

    @Requires
    private DataProvider dataProvider;

    private static void  processCallback(BioRespBuilder.Builder brsp, Callback callback) throws Exception {
        if(callback != null) {
            //String responseJson = Jsons.encode(brsp.build());
            callback.run(brsp);
        }
    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");

        if(routeMap == null) {
            routeMap = new HashMap<>();

            routeMap.put(BioRoute.UNKNOWN, new BioRouteHandler<BioRequest>() {
                @Override
                public void handle(BioRequest request, Callback callback) throws Exception {
                    processCallback(
                        BioRespBuilder.anError()
                            .exception(new BioError.BadRequestType(request.getRequestType()))
                    , callback);
                }
            });

            routeMap.put(BioRoute.PING, new BioRouteHandler<BioRequest>() {
                @Override
                public void handle(BioRequest request, Callback callback) throws Exception {
                    BioRespBuilder.Data responseBuilder = BioRespBuilder.data();
                    processCallback(responseBuilder, callback);
                }
            });

            routeMap.put(BioRoute.CRUD_DATASET_GET, new BioRouteHandler<BioRequestJStoreGetDataSet>() {
                @Override
                public void handle(BioRequestJStoreGetDataSet request, Callback callback) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_DATASET_GET);
                    BioRespBuilder.Data responseBuilder = dataProvider.getDataSet(request);
                    processCallback(responseBuilder, callback);
                }
            });

            routeMap.put(BioRoute.CRUD_RECORD_GET, new BioRouteHandler<BioRequestJStoreGetRecord>() {
                @Override
                public void handle(BioRequestJStoreGetRecord request, Callback callback) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_RECORD_GET);
                    BioRespBuilder.Data responseBuilder = dataProvider.getRecord(request);
                    processCallback(responseBuilder, callback);
                }
            });

            routeMap.put(BioRoute.CRUD_DATASET_POST, new BioRouteHandler<BioRequestJStorePost>() {
                @Override
                public void handle(BioRequestJStorePost request, Callback callback) throws Exception {
                    LOG.debug("Processing {} request...", BioRoute.CRUD_DATASET_POST);
                    BioRespBuilder.Data responseBuilder = dataProvider.postDataSet(request);
                    processCallback(responseBuilder, callback);
                }
            });

        }

        this.redy = true;
        LOG.debug("Started");
    }

    @Invalidate
    public void doStop() throws Exception {
        LOG.debug("Stoping...");
        this.redy = false;
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
    public void route(BioRequest request, Callback callback) throws Exception {
        BioRoute type = BioRoute.getType(request.getRequestType());
        routeMap.get(type).handle(request, callback);

    }
}
