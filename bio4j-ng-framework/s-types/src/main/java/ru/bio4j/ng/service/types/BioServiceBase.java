package ru.bio4j.ng.service.types;

import org.apache.felix.ipojo.annotations.*;
import ru.bio4j.ng.service.api.BioConfig;
import ru.bio4j.ng.service.api.BioService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import ru.bio4j.ng.service.api.Configurator;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class BioServiceBase<T> implements BioService {

    protected volatile boolean configIsRedy;
    protected volatile boolean redy;
    protected Configurator<T> getConfigurator(){
        return null;
    }

    protected EventAdmin getEventAdmin(){
        return null;
    }

    public boolean isRedy() {
        return redy;
    }

    private void fireEventConfigUpdated(final String configUpdatedEventName) throws Exception {
//        LOG.debug("Sending event...");
//        eventAdmin.postEvent(new Event("bio-config-updated", new HashMap<String, Object>()));
//        LOG.debug("Event sent.");

        // Откладываем отправку события чтобы успел инициализироваться логгер
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(new Runnable() {
            @Override
            public void run() {
                getEventAdmin().postEvent(new Event(configUpdatedEventName, new HashMap<String, Object>()));
            }
        }, 1, TimeUnit.SECONDS);

    }

    protected void doOnUpdated(Dictionary conf, String configUpdatedEventName) throws Exception {
        getConfigurator().update(conf);
        configIsRedy = getConfigurator().isUpdated();
        if(configIsRedy) {
            fireEventConfigUpdated(configUpdatedEventName);
        }
    }

    public T getConfig() {
        if(!getConfigurator().isUpdated()) {
            return null;
        }
        return getConfigurator().getConfig();
    }

    public boolean configIsRedy() {
        return configIsRedy;
    }

}
