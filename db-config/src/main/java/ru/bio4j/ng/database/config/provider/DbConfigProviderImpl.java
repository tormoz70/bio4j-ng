package ru.bio4j.ng.database.config.provider;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.SQLContextConfig;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.ServiceBase;

import java.util.Dictionary;

@Component(managedservice="db.config")
@Instantiate
@Provides(specifications = DbConfigProvider.class)
public class DbConfigProviderImpl extends ServiceBase<SQLContextConfig> implements DbConfigProvider {
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
        if(LOG.isDebugEnabled())
            LOG.debug("Starting...");
        this.ready = true;
        if(LOG.isDebugEnabled())
            LOG.debug("Started");
    }

    @Invalidate
    public void stop() throws Exception {
        if(LOG.isDebugEnabled())
            LOG.debug("Stoping...");
        this.ready = false;
        if(LOG.isDebugEnabled())
            LOG.debug("Stoped.");
    }

}
