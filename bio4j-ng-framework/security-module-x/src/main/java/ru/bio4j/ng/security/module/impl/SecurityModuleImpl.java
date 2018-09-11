package ru.bio4j.ng.security.module.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioSecurityModule;
import ru.bio4j.ng.service.api.DbConfigProvider;
import ru.bio4j.ng.service.api.SecurityProvider;
import ru.bio4j.ng.service.types.BioModuleBase;
import ru.bio4j.ng.service.api.SQLContextConfig;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Dictionary;

@Component
@Instantiate
@Provides(specifications = BioSecurityModule.class,
        properties = {@StaticServiceProperty(
                name = "bioModuleKey",
                value = "security", // key must be always "security" for security module
                type = "java.lang.String"
        )})
public class SecurityModuleImpl extends BioModuleBase implements BioSecurityModule {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityModuleImpl.class);

    @Requires
    private EventAdmin eventAdmin;
    @Requires
    private SecurityProvider securityProvider;
    @Requires
    private DbConfigProvider dbConfigProvider;

    @Override
    public String getKey() {
        return "security";
    }

    @Override
    protected EventAdmin getEventAdmin() {
        return eventAdmin;
    }

    private BundleContext bundleContext;

    @Context
    public void setBundleContext(BundleContext context) {
        this.bundleContext = context;
        LOG.debug("Field \"bundleContext\" - updated!");
    }

    @Override
    protected BundleContext bundleContext() {
        return bundleContext;
    }

    @Override
    public String getDescription() {
        return "Security module";
    }

    protected SQLContext createSQLContext() throws Exception {
        SQLContextConfig cfg = dbConfigProvider.getConfig();
        return ru.bio4j.ng.database.pgsql.SQLContextFactory.create(cfg);
    }

    @Override
    public User login(final String login, final String remoteIP, final String remoteClient) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public User restoreUser(String stokenOrUsrUid) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public User getUser(final String stoken, final String remoteIP, final String remoteClient) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public void logoff(final String stoken, final String remoteIP) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public Boolean loggedin(final String stoken, final String remoteIP, final String remoteClient) throws Exception {
        throw new NotImplementedException();
    }

    @Updated
    public synchronized void updated(Dictionary conf) throws Exception {
        doOnUpdated(conf, "security-config-updated");
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        fireEventModuleUpdated();
        LOG.debug("Started");
    }

    public SecurityProvider getSecurityProvider() {
        return securityProvider;
    }
}
