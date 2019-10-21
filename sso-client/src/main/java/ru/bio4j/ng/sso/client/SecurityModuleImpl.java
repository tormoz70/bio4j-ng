package ru.bio4j.ng.sso.client;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.AppServiceBase;

import java.util.Dictionary;

@Component(managedservice="sso.client.config")
@Instantiate
@Provides(specifications = SecurityService.class)
public class SecurityModuleImpl extends AppServiceBase<SsoClientConfig> implements SecurityService {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityModuleImpl.class);

    private volatile SsoClient ssoClient;


    private synchronized void initSsoClient() {
        if(ssoClient == null) {
            ssoClient = SsoClient.create(getConfig().getSsoServiceUrl());
        }
    }

    private SsoClient getSsoClient() {
        initSsoClient();
        return ssoClient;
    }

    @Requires
    private EventAdmin eventAdmin;

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

//    @Override
//    protected SQLContext createSQLContext() {
//        return null;
//    }

    @Override
    public User login(final BioQueryParams qprms) {
        return getSsoClient().login(qprms);
    }

    @Override
    public User getUser(final BioQueryParams qprms) {
        return getSsoClient().curUser(qprms);
    }

    @Override
    public User restoreUser(String stokenOrUsrUid) {
        return getSsoClient().restoreUser(stokenOrUsrUid, null, null);
    }

    @Override
    public void logoff(final BioQueryParams qprms) {
        getSsoClient().logoff(qprms);
    }

    @Override
    public Boolean loggedin(final BioQueryParams qprms) {
        return getSsoClient().loggedin(qprms);
    }


    @Updated
    public synchronized void updated(Dictionary conf) throws Exception {
        doOnUpdated(conf, "security-config-updated");
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        //fireEventModuleUpdated();
        LOG.debug("Started");
    }

}
