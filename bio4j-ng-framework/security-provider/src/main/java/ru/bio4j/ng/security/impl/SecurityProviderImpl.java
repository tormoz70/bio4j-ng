package ru.bio4j.ng.security.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

@Component
@Instantiate
@Provides(specifications = SecurityProvider.class)
public class SecurityProviderImpl extends BioServiceBase implements SecurityProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityProviderImpl.class);

    @Requires
    private ModuleProvider moduleProvider;

    private final String securityModuleKey = "security";

    private BioSecurityModule _securityModule = null;
    private BioSecurityModule getSecurityModule() throws Exception {
        if(_securityModule != null)
            return _securityModule;
        LOG.debug("getting module {}...", securityModuleKey);
        _securityModule = moduleProvider.getSecurityModule(securityModuleKey);
        if(_securityModule != null) {
            LOG.debug("module {} found!", securityModuleKey);
        } else
            LOG.debug("module {} not found!", securityModuleKey);
        return _securityModule;
    }


    @Override
    public User getUser(final String stoken, final String remoteIP, final String remoteClient) throws Exception {
        if(isNullOrEmpty(stoken))
            throw new BioError.Login.Unauthorized();

        User newUsr = getSecurityModule().getUser(stoken, remoteIP, remoteClient);
        if(newUsr == null)
            throw new BioError.Login.Unauthorized();
        return newUsr;
    }

    @Override
    public User login(final String login, final String remoteIP, final String remoteClient) throws Exception {
        if(isNullOrEmpty(login))
            throw new BioError.Login.Unauthorized();

        User newUsr = getSecurityModule().login(login, remoteIP, remoteClient);
        if(newUsr == null)
            throw new BioError.Login.Unauthorized();
        return newUsr;
    }

    @Override
    public void logoff(final String stoken, final String remoteIP) throws Exception {
        getSecurityModule().logoff(stoken, remoteIP);
    }

    @Override
    public Boolean loggedin(final String stoken, final String remoteIP, final String remoteClient) throws Exception {
        return getSecurityModule().loggedin(stoken, remoteIP, remoteClient);
    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");
        this.ready = true;
        LOG.debug("Started");
    }

    @Invalidate
    public void doStop() throws Exception {
        LOG.debug("Stopping...");
        this.ready = false;
        LOG.debug("Stopped.");
    }

    @Subscriber(
            name="security.handler.subscriber",
            topics="bio-config-updated")
    public void receive(Event e) throws Exception {
        LOG.debug("Config updated event received!!!");
        //doStop();
        //doStart();
    }

}
