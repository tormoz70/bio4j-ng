package ru.bio4j.ng.module.provider.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioModuleHelper;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.util.HashMap;
import java.util.Map;

@Component
@Instantiate
@Provides(specifications = ModuleProvider.class)
public class ModuleProviderImpl extends BioServiceBase implements ModuleProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ModuleProviderImpl.class);

    @Context
    private BundleContext bundleContext;
    private Map<String, BioModule> modules = new HashMap<>();

    private <T extends BioModule> T getModule(String key, Class<T> clazz) throws Exception {
        LOG.debug("About getModule by key - \"{}\"...", key);
        T rslt = (T)modules.get(key);
        if(rslt == null) {
            LOG.debug("Module \"{}\" not in cache, searching...", key);
            rslt = BioModuleHelper.lookupService(bundleContext, clazz, key);
            modules.put(key, rslt);
            LOG.debug("Module \"{}\" found and putted in cache.", key);
        } else
            LOG.debug("Module \"{}\" found in cache.", key);
        return rslt;
    }

    @Override
    public BioAppModule getAppModule(String key) throws Exception {
        return getModule(key, BioAppModule.class);
    }

    @Override
    public BioSecurityModule getSecurityModule(String key) throws Exception {
        return getModule(key, BioSecurityModule.class);
    }

    @Override
    public BioFCloudApiModule getFCloudApiModule(String key) throws Exception {
        return getModule(key, BioFCloudApiModule.class);
    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");
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
            name="module.provider.subscriber",
            topics="bio-module-updated")
    public void receive(Event e) throws Exception {
        String updatedModuleKey = (String)e.getProperty("bioModuleKey");
        LOG.debug("Module \"{}\" updated event recived!!!", updatedModuleKey);
        if(modules.containsKey(updatedModuleKey)) {
            modules.remove(updatedModuleKey);
            LOG.debug("Module \"{}\" removed from cache!!!", updatedModuleKey);
        }

    }

}
