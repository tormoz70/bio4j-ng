package ru.bio4j.ng.config;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component(managedservice="bio4j.config")
@Instantiate
@Provides(specifications = ConfigProvider.class)
public class ConfigProviderImpl extends BioServiceBase<BioConfig> implements ConfigProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigProviderImpl.class);

    @Requires
    private EventAdmin eventAdmin;

    @Override
    protected EventAdmin getEventAdmin(){
        return eventAdmin;
    }

    @Updated
    public synchronized void updated(Dictionary conf) throws Exception {
        doOnUpdated(conf, "bio-config-updated");
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