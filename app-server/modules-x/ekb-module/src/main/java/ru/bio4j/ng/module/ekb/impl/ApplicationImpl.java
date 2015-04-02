package ru.bio4j.ng.module.ekb.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLContextConfig;
import ru.bio4j.ng.database.oracle.SQLContextFactory;
import ru.bio4j.ng.service.api.BioModule;
import ru.bio4j.ng.module.commons.BioModuleBase;

@Component
@Instantiate
@Provides(specifications = BioModule.class,
        properties = {@StaticServiceProperty(
                name = "bioModuleKey",
                value = "ekb",
                type = "java.lang.String"
        )})
public class ApplicationImpl extends BioModuleBase {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationImpl.class);

    @Requires
    private EventAdmin eventAdmin;

    @Override
    protected EventAdmin getEventAdmin() {
        return eventAdmin;
    }

    @Override
    protected String getSelfModuleKey() {
        return "ekb";
    }

    @Context
    private BundleContext bundleContext;

    @Override
    protected BundleContext bundleContext() {
        return bundleContext;
    }

    @Override
    protected SQLContext createSQLContext(SQLContextConfig config) throws Exception {
        return SQLContextFactory.create(config);
    }

    @Override
    public String getDescription() {
        return "E-Kinobilet application";
    }


    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        fireEventModuleUpdated();
        LOG.debug("Started");
    }

}
