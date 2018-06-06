package ru.bio4j.ng.fcloud.provider;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.util.Dictionary;

@Component
@Instantiate
@Provides(specifications = FCloudProvider.class)
public class FCloudProviderImpl extends BioServiceBase implements FCloudProvider {
    private static final Logger LOG = LoggerFactory.getLogger(FCloudProviderImpl.class);

    @Requires
    private EventAdmin eventAdmin;

    @Requires
    private ModuleProvider moduleProvider;

    @Requires
    private ConfigProvider configProvider;

    @Override
    protected EventAdmin getEventAdmin(){
        return eventAdmin;
    }

    private BioFCloudApiModule fcloudApi;

    @Override
    public BioFCloudApiModule getApi() throws Exception {
        if(fcloudApi == null) {
            String fcloudApiModuleName = configProvider.getConfig().getFcloudApi();
            fcloudApi = moduleProvider.getFCloudApiModule(fcloudApiModuleName);
        }
        return fcloudApi;
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        if(!configProvider.configIsReady()) {
            LOG.info("Config is not ready! Waiting...");
            return;
        }
        this.ready = true;
        LOG.debug("Started");
    }

    @Invalidate
    public void stop() throws Exception {
        LOG.debug("Stoping...");
        this.ready = false;
        LOG.debug("Stoped.");
    }

}
