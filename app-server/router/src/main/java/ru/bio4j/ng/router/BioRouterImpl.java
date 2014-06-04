package ru.bio4j.ng.router;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;
import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.api.DataProvider;
import ru.bio4j.ng.service.api.BioRespBuilder;
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
            String responseJson = Jsons.encode(brsp.build());
            callback.run(responseJson);
        }
    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");

        if(routeMap == null) {
            routeMap = new HashMap<>();

            routeMap.put(BioRoute.UNKNOWN, new BioRouteHandler() {
                    @Override
                    public void handle(String requestType, String requestBody, Callback callback) throws Exception {
                        BioRespBuilder.AnError brsp = BioRespBuilder.create(BioRespBuilder.AnError.class);
                        brsp.addError(new Exception(String.format("Value of argument \"requestType\":\"%s\" is unknown!", requestType)));
                        processCallback(brsp, callback);
                    }
                });

            routeMap.put(BioRoute.CRUD_DATA_GET, new BioRouteHandler() {
                    @Override
                    public void handle(String requestType, String requestBody, Callback callback) throws Exception {
                        LOG.debug("Processing {} request...", BioRoute.CRUD_DATA_GET);
                        LOG.debug("Lets try to decode {} from json: \n{}", BioRequestJStoreGet.class.getName(), requestBody);
                        BioRequestJStoreGet request = Jsons.decode(requestBody, BioRequestJStoreGet.class);
                        LOG.debug("BioRequestJStoreGet object restored.");
//                        request.setOrigJson(requestBody);
                        BioRespBuilder.Data responseBuilder = dataProvider.getData(request);
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
    public void route(String requestType, String requestBody, Callback callback) throws Exception {
        BioRoute type = BioRoute.getType(requestType);
        routeMap.get(type).handle(requestType, requestBody, callback);

    }
}
