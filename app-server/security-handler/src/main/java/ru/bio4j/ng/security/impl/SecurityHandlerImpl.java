package ru.bio4j.ng.security.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLActionScalar;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLContextConfig;
import ru.bio4j.ng.database.api.SQLCursor;
import ru.bio4j.ng.database.doa.SQLContextFactory;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.module.api.BioModule;
import ru.bio4j.ng.module.api.BioModuleHelper;
import ru.bio4j.ng.service.api.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;


@Component
@Instantiate
@Provides(specifications = DataProvider.class)
public class SecurityHandlerImpl extends BioServiceBase implements SecurityHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityHandlerImpl.class);

    @Context
    private BundleContext bundleContext;
    private SQLContext globalSQLContext;

    private SQLContext selectSQLContext(BioModule module) throws Exception {
        LOG.debug("About selecting sqlContext...");
        SQLContext ctx = module.getSQLContext();
        if(ctx == null) {
            LOG.debug("Local sqlContext not defined. Global sqlContext will be used.");
            ctx = globalSQLContext;
        } else
            LOG.debug("Local sqlContext defined and will be used.");

        return ctx;
    }

    private Map<String, BioModule> modules = new HashMap<>();
    private BioModule getModule(String key) throws Exception {
        BioModule rslt = modules.get(key);
        if(rslt == null) {
            rslt = BioModuleHelper.lookupService(bundleContext, key);
            modules.put(key, rslt);
        }
        return rslt;
    }

    @Override
    public User getUser(String login) throws Exception {
        BioModule module = getModule("bio");
        BioCursor cursor = module.getCursor("get-user");

        return globalSQLContext.execBatch(new SQLActionScalar<User>() {
            @Override
            public User exec(SQLContext context, Connection conn) throws Exception {
                User rslt = new User();
                LOG.debug("Opening cursor...");
                try(SQLCursor c = context.CreateCursor()
                        .init(conn, "select username from user_users", null)
                        .open()) {
                    LOG.debug("Cursor opened...");
                    while (c.reader().read()){
                        LOG.debug("Reading field USERNAME...");
                        String s = c.reader().getValue("USERNAME", String.class);
//                        rslt.append(s+";");
                    }
                }
                return rslt;
            }
        });
    }

    @Requires
    private ConfigProvider configProvider;

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");

        if(!configProvider.configIsRedy()) {
            LOG.info("Config is not redy! Waiting...");
            return;
        }

        if(globalSQLContext == null) {
            LOG.debug("Creating SQLContext (poolName:{})...", configProvider.getConfig().getPoolName());
            try {
                SQLContextConfig cfg = new SQLContextConfig();
                Utl.applyValuesToBean(configProvider.getConfig(), cfg);
                globalSQLContext = SQLContextFactory.create(cfg);
            } catch (Exception e) {
                LOG.error("Error while creating SQLContext!", e);
            }
        } else {

        }

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
            name="crud.handler.subscriber",
            topics="bio-config-updated")
    public void receive(Event e) throws Exception {
        LOG.debug("Config updated event recived!!!");
        doStop();
        doStart();
    }

}
