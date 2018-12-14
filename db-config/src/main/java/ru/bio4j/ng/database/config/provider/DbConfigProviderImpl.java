package ru.bio4j.ng.database.config.provider;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;
import ru.bio4j.ng.service.api.SQLContextConfig;

import java.util.Dictionary;

@Component(managedservice="db.config")
@Instantiate
@Provides(specifications = DbConfigProvider.class)
public class DbConfigProviderImpl extends BioServiceBase<SQLContextConfig> implements DbConfigProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DbConfigProviderImpl.class);

    @Requires
    private EventAdmin eventAdmin;

    @Context
    private BundleContext bundleContext;

    @Override
    protected BundleContext bundleContext() {
        return bundleContext;
    }

    @Override
    protected EventAdmin getEventAdmin(){
        return eventAdmin;
    }

    @Updated
    public synchronized void updated(Dictionary conf) throws Exception {
        doOnUpdated(conf, "db-config-updated");
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
