package ru.bio4j.ng.crudhandlers.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.api.BioRoute;
import ru.bio4j.ng.service.types.BioServiceBase;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@Component
@Instantiate
@Provides(specifications = DataProvider.class)
public class DataProviderImpl extends BioServiceBase implements DataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DataProviderImpl.class);

    private Map<BioRoute, ProviderAn> providerMap;

    @Context
    private BundleContext bundleContext;

    @Requires
    private SecurityProvider securityProvider;
    @Requires
    private ModuleProvider moduleProvider;
    @Requires
    private SQLContextProvider sqlContextProvider;
    @Requires
    private FCloudProvider fcloudProvider;
//    @Requires
//    private ContentResolver contentResolver;


    private BioAppModule getActualModule(final BioRequest request) throws Exception {
        String altModuleKey = Utl.extractModuleKey(request.getBioCode());
        String defaultModuleKey = request.getModuleKey();
        String moduleKey = (Strings.isNullOrEmpty(altModuleKey) ? defaultModuleKey : altModuleKey);
        return moduleProvider.getAppModule(moduleKey);
    }

    private SQLContext getActualContext(final BioRequest request, final BioAppModule module) throws Exception {
        SQLContext ctx = sqlContextProvider.selectContext(module);
        if (ctx == null) {
            String defaultModuleKey = request.getModuleKey();
            BioAppModule ctxModule = moduleProvider.getAppModule(defaultModuleKey);
            ctx = sqlContextProvider.selectContext(ctxModule);
        }
        return ctx;
    }

    @Override
    public void processRequest(BioRoute route, final BioRequest request, final HttpServletResponse response) throws Exception {
        final BioAppModule module = getActualModule(request);
        final SQLContext context = getActualContext(request, module);
        ProviderAn provider = providerMap.get(route);
        if(provider != null) {
            provider.init(module, null, context);
            provider.process(request, response);
        } else
            throw new IllegalArgumentException(String.format("Для запроса %s не определен обработчик!", route));
    }

//    @Override
//    public BioRespBuilder.Builder processRequest(BioRoute route, final BioRequest request) throws Exception {
//        final BioAppModule module = getActualModule(request);
//        final SQLContext context = getActualContext(request, module);
//        ProviderAn provider = providerMap.get(route);
//        if(provider != null) {
//            provider.init(module, context);
//            return provider.process(request, re);
//        } else
//            throw new IllegalArgumentException(String.format("Для запроса %s не определен обработчик!", route));
//        //final BioRespBuilder.Builder result = BioRespBuilder.dataBuilder().exception(new BioError.BadRequestType(request.getRequestType()));
//        //return result;
//    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");
        if(providerMap == null){
            providerMap = new HashMap<>();
            providerMap.put(BioRoute.CRUD_DATASET_GET, new ProviderGetDataset());
            providerMap.put(BioRoute.CRUD_DATASET_EXP, new ProviderExpDataset());
            providerMap.put(BioRoute.CRUD_RECORD_GET, new ProviderGetRecord());
            providerMap.put(BioRoute.CRUD_DATASET_POST, new ProviderPostDataset());
            providerMap.put(BioRoute.CRUD_EXEC, new ProviderExec());
            providerMap.put(BioRoute.CRUD_JSON_GET, new ProviderGetJson());
            providerMap.put(BioRoute.CRUD_FILE_GET, new ProviderGetFile());
            providerMap.put(BioRoute.CRUD_FCLOUD, new ProviderFCloud(fcloudProvider));
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
            name="crud.handler.subscriber",
            topics="bio-config-updated")
    public void receive(Event e) throws Exception {
        LOG.debug("Config updated event recived!!!");
        doStop();
        doStart();
    }

}
