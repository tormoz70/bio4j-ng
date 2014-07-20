package ru.bio4j.ng.module.provider.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.module.commons.BioModuleHelper;
import ru.bio4j.ng.service.api.BioModule;
import ru.bio4j.ng.service.api.ModuleProvider;
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

    @Override
    public BioModule getModule(String key) throws Exception {
        LOG.debug("About getModule by key - \"{}\"...", key);
        BioModule rslt = modules.get(key);
        if(rslt == null) {
            rslt = BioModuleHelper.lookupService(bundleContext, key);
            modules.put(key, rslt);
        }
        return rslt;
    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");
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
