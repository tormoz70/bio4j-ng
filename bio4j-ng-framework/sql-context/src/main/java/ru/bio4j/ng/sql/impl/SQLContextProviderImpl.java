package ru.bio4j.ng.sql.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.service.api.BioAppModule;
import ru.bio4j.ng.service.api.SQLContextProvider;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;


@Component
@Instantiate
@Provides(specifications = SQLContextProvider.class)
public class SQLContextProviderImpl extends BioServiceBase implements SQLContextProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SQLContextProviderImpl.class);

    @Override
    public SQLContext selectContext(BioAppModule module) throws Exception {
        LOG.debug("About selecting sqlContext...");
        SQLContext ctx = module.getSQLContext();
        if(ctx == null) {
            LOG.debug("Local sqlContext not defined. Global sqlContext will be used.");
            //ctx = globalSQLContext;
        } else
            LOG.debug("Local sqlContext defined and will be used.");

        return ctx;
    }

    @Requires
    private ConfigProvider configProvider;

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");

        if(!configProvider.configIsReady()) {
            LOG.info("Config is not ready! Waiting...");
            return;
        }
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
            name="crud.handler.subscriber",
            topics="bio-config-updated")
    public void receive(Event e) throws Exception {
        LOG.debug("Config updated event recived!!!");
        doStop();
        doStart();
    }

}
