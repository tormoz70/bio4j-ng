package ru.bio4j.ng.module.cmon.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.BioModule;
import ru.bio4j.ng.module.commons.BioModuleBase;

@Component
@Instantiate
@Provides(specifications = BioModule.class,
        properties = {@StaticServiceProperty(
                name = "bioModuleKey",
                value = "cmon",
                type = "java.lang.String"
        )})
public class CmonApplicationImpl extends BioModuleBase {
    private static final Logger LOG = LoggerFactory.getLogger(CmonApplicationImpl.class);

    @Requires
    private EventAdmin eventAdmin;

    @Override
    protected EventAdmin getEventAdmin() {
        return eventAdmin;
    }

    @Override
    protected String getSelfModuleKey() {
        return "cmon";
    }

    @Context
    private BundleContext bundleContext;

    @Override
    protected BundleContext bundleContext() {
        return bundleContext;
    }


    @Override
    public String getDescription() {
        return "CMON application";
    }


    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        fireEventModuleUpdated();
        LOG.debug("Started");
    }

}
