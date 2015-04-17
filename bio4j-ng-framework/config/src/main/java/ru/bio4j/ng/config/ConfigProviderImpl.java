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
public class ConfigProviderImpl extends BioServiceBase implements ConfigProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigProviderImpl.class);

    private Configurator<BioConfig> configurator = new Configurator<>(BioConfig.class);
    private boolean configIsRedy;

    @Requires
    private EventAdmin eventAdmin;

    private void fireEventConfigUpdated() throws Exception {
//        LOG.debug("Sending event...");
//        eventAdmin.postEvent(new Event("bio-config-updated", new HashMap<String, Object>()));
//        LOG.debug("Event sent.");

        // Откладываем отправку события чтобы успел инициализироваться логгер
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(new Runnable() {
            @Override
            public void run() {
                LOG.debug("Sending event...");
                eventAdmin.postEvent(new Event("bio-config-updated", new HashMap<String, Object>()));
                LOG.debug("Event sent.");
            }
        }, 1, TimeUnit.SECONDS);

    }

    @Updated
    public void updated(Dictionary conf) throws Exception {
        LOG.debug("Updating config...");
        configurator.update(conf);
        configIsRedy = configurator.isUpdated();
        if(configIsRedy) {
            LOG.debug("Config updated.");
            fireEventConfigUpdated();
        } else
            LOG.debug("Config not updated.");
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        this.redy = true;
        LOG.debug("Started");
    }

    @Invalidate
    public void stop() throws Exception {
        LOG.debug("Stoping...");
        this.redy = false;
        LOG.debug("Stoped.");
    }

    @Override
    public BioConfig getConfig() {
        if(!configurator.isUpdated()) {
            LOG.info("Config is not loaded! Try later...");
            return null;
        }
        return configurator.getConfig();
    }

    @Override
    public boolean configIsRedy() {
        return configIsRedy;
    }


}
