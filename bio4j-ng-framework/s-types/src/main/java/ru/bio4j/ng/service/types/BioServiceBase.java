package ru.bio4j.ng.service.types;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import ru.bio4j.ng.service.api.BioService;

import java.lang.reflect.ParameterizedType;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class BioServiceBase<T> {
    private static final Logger LOG = LoggerFactory.getLogger(BioServiceBase.class);

    protected abstract BundleContext bundleContext();

    protected volatile boolean configIsReady;
    protected volatile boolean ready;

    private Configurator<T> configurator = null;

    private Class<T> typeOfConfig;
    protected Configurator<T> getConfigurator(){
        if(configurator == null) {
            try {
                typeOfConfig = (Class<T>)
                        ((ParameterizedType) getClass()
                                .getGenericSuperclass())
                                .getActualTypeArguments()[0];
                configurator = new Configurator<>(typeOfConfig);
            } catch (ClassCastException e){
                configurator = null;
            }
        }
        return configurator;
    }

    protected EventAdmin getEventAdmin(){
        return null;
    }

    private void fireEventServiceUpdated(final String configUpdatedEventName) throws Exception {
//        LOG.debug("Sending event...");
//        eventAdmin.postEvent(new Event("bio-config-updated", new HashMap<String, Object>()));
//        LOG.debug("Event sent.");

        // Откладываем отправку события чтобы успел инициализироваться логгер
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(new Runnable() {
            @Override
            public void run() {
                LOG.debug("Sending event [{}] for service \"{}\"...", configUpdatedEventName, this.getClass().getName());
                getEventAdmin().postEvent(new Event(configUpdatedEventName, new HashMap<String, Object>()));
                LOG.debug("Event [{}] for service \"{}\" sent.", configUpdatedEventName, this.getClass().getName());
            }
        }, 1, TimeUnit.SECONDS);

    }

    protected void fireEventServiceUpdated() throws Exception {
        fireEventServiceUpdated("bio-service-updated");
    }

    protected void fireEventServiceStarted() throws Exception {
        fireEventServiceUpdated("bio-service-started");
    }

    protected void doOnUpdated(Dictionary conf, String eventName) throws Exception {
        if(!Utl.confIsEmpty(conf)) {
            Configurator<T> configurator = getConfigurator();
            if(configurator != null) {
                configurator.update(conf);
                configIsReady = configurator.isUpdated();
                if (configIsReady) {
                    if(Strings.isNullOrEmpty(eventName))
                        fireEventServiceUpdated();
                    else
                        fireEventServiceUpdated(eventName);
                }
            }
        }
    }

    protected void doOnUpdated(Dictionary conf) throws Exception {
        doOnUpdated(conf, null);
    }

    public T getConfig() {
        Configurator<T> configurator = getConfigurator();
        if(configurator != null) {
            if (!configurator.isUpdated()) {
                return null;
            }
            return configurator.getConfig();
        }
        return null;
    }

    public boolean configIsReady() {
        return configIsReady;
    }

}
