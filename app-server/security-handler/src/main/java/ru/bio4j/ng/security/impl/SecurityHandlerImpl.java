package ru.bio4j.ng.security.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLActionScalar;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLContextConfig;
import ru.bio4j.ng.database.api.SQLCursor;
import ru.bio4j.ng.database.doa.SQLContextFactory;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.module.commons.BioModuleHelper;
import ru.bio4j.ng.service.api.BioModule;
import ru.bio4j.ng.service.api.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;


@Component
@Instantiate
@Provides(specifications = SecurityHandler.class)
public class SecurityHandlerImpl extends BioServiceBase implements SecurityHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityHandlerImpl.class);

    @Context
    private BundleContext bundleContext;

    @Requires
    private ModuleProvider moduleProvider;
    @Requires
    private SQLContextProvider sqlContextProvider;

    @Override
    public User getUser(final String login) throws Exception {
        if(isNullOrEmpty(login) || !login.equals("root/root"))
            return null;

        BioModule module = moduleProvider.getModule("bio");
        BioCursor cursor = module.getCursor("get-user");
        SQLContext globalSQLContext = sqlContextProvider.globalContext();
        return globalSQLContext.execBatch(new SQLActionScalar<User>() {
            @Override
            public User exec(SQLContext context, Connection conn) throws Exception {
                LOG.debug("User {} logging in...", login);
                try(SQLCursor c = context.CreateCursor()
                        .init(conn, "select username from user_users", null)
                        .open()) {
                    if (c.reader().read()){
                        LOG.debug("User found!");
                        String s = c.reader().getValue("USERNAME", String.class);
                        User usr = new User();
                        usr.setUid("test-user-uid");
                        usr.setLogin("root");
                        usr.setFio("Test User FIO");
                        usr.setRoles(new String[]{"*"});
                        usr.setGrants(new String[] {"*"});
                        LOG.debug("User found: {}", Utl.buildBeanStateInfo(usr, "User", "  "));
                        return usr;
                    }
                }
                LOG.debug("User not found!");
                return null;
            }
        });
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
            name="crud.handler.subscriber",
            topics="bio-config-updated")
    public void receive(Event e) throws Exception {
        LOG.debug("Config updated event recived!!!");
//        doStop();
//        doStart();
    }

}
