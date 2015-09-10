package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.service.api.BioService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import ru.bio4j.ng.service.api.Configurator;

import java.lang.reflect.ParameterizedType;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class BioServiceBase<T> implements BioService {

    protected volatile boolean configIsReady;
    protected volatile boolean ready;

    private Configurator<T> configurator = null;

    private Class<T> typeOfConfig;
    protected Configurator<T> getConfigurator(){
        if(configurator == null) {
            typeOfConfig = (Class<T>)
                    ((ParameterizedType) getClass()
                            .getGenericSuperclass())
                            .getActualTypeArguments()[0];
            configurator = new Configurator<>(typeOfConfig);
        }
        return configurator;
    }

    protected EventAdmin getEventAdmin(){
        return null;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    protected void fireEventConfigUpdated(final String configUpdatedEventName) throws Exception {
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
        if(!Utl.confIsEmpty(conf)) {
            getConfigurator().update(conf);
            configIsReady = getConfigurator().isUpdated();
            if (configIsReady) {
                fireEventConfigUpdated(configUpdatedEventName);
            }
        }
    }

    public T getConfig() {
        if(!getConfigurator().isUpdated()) {
            return null;
        }
        return getConfigurator().getConfig();
    }

    public boolean configIsReady() {
        return configIsReady;
    }

}
