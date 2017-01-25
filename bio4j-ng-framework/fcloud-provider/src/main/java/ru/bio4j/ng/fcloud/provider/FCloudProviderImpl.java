package ru.bio4j.ng.fcloud.provider;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.util.Dictionary;

@Component(managedservice="fcloud.config")
@Instantiate
@Provides(specifications = ConfigProvider.class)
public class FCloudProviderImpl extends BioServiceBase<FCloudConfig> implements FCloudProvider {
    private static final Logger LOG = LoggerFactory.getLogger(FCloudProviderImpl.class);

    @Requires
    private EventAdmin eventAdmin;

    @Override
    protected EventAdmin getEventAdmin(){
        return eventAdmin;
    }

    @Updated
    public synchronized void updated(Dictionary conf) throws Exception {
        doOnUpdated(conf, "fcloud-config-updated");
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
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
