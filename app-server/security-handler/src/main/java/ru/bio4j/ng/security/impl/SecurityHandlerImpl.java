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
import ru.bio4j.ng.database.api.SQLCursor;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioModule;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    private ConcurrentMap<String, User> onlineUsers = new ConcurrentHashMap<>();

    private void storeUser(User user) throws Exception {
        User existsUser = onlineUsers.get(user.getUid());
        if(existsUser != null) {
            Utl.applyValuesToBean(user, existsUser);
            return;
        }
        onlineUsers.put(user.getUid(), user);
    }

    private User userIsOnline(String userUID) {
        return onlineUsers.get(userUID);
    }

    private User removeUser(String userUID) {
        return onlineUsers.remove(userUID);
    }

    @Override
    public User getUser(final String loginOrUid) throws Exception {
        if(isNullOrEmpty(loginOrUid))
            throw new BioError.Login.BadLogin();
        final String uid = loginOrUid.contains("/") ? null : loginOrUid;
        final String login = loginOrUid.contains("/") ? loginOrUid : null;

        if(!isNullOrEmpty(uid)){
            User onlineUser = userIsOnline(uid);
            if(onlineUser != null){
                LOG.debug("User with uid \"{}\" alredy logged in as \"{}\".", uid, onlineUser.getLogin());
                return onlineUser;
            } else
                throw new BioError.Login.LoginExpired();
        }

        if(!login.equals("root/root"))
            throw new BioError.Login.BadLogin();

        BioModule module = moduleProvider.getModule("bio");
        BioCursor cursor = module.getCursor("bio@get-user");
        SQLContext globalSQLContext = sqlContextProvider.globalContext();
        User newUsr = globalSQLContext.execBatch(new SQLActionScalar<User>() {
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
                    usr.setRoles("*");
                    usr.setGrants("*"
                    );
                    LOG.debug("User found: {}", Utl.buildBeanStateInfo(usr, "User", "  "));
                    return usr;
                }
            }
            LOG.debug("User not found!");
            return null;
            }
        });
        storeUser(newUsr);
        return newUsr;
    }

    @Override
    public void logoff(String uid) throws Exception {
        removeUser(uid);
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
