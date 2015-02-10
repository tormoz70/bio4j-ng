package ru.bio4j.ng.security.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioModule;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;
import ru.bio4j.ng.service.types.BioServletBase;

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

    private void storeUser(final User user) throws Exception {
        User existsUser = onlineUsers.get(user.getUid());
        if(existsUser != null) {
            Utl.applyValuesToBean(user, existsUser);
            return;
        }
        onlineUsers.put(String.format("%s-%s", user.getModuleKey(), user.getUid()), user);
    }

    private User userIsOnline(final String moduleKey, final String userUID) {
        return onlineUsers.get(String.format("%s-%s", moduleKey, userUID));
    }

    private User removeUser(final String moduleKey, final String userUID) {
        return onlineUsers.remove(String.format("%s-%s", moduleKey, userUID));
    }

    //private static final String BIO_ANONYMOUS_USER_UID = "bio-anonymous-user-uid";
    private static final String ROOT_USER_LOGIN = "root/root";
    private static final String ROOT_USER_UID = "root-user-uid";

    private User detectAnonymouse(String moduleKey, String userUidOrLogin) throws Exception {
        if(BioServletBase.BIO_ANONYMOUS_USER_LOGIN.equals(userUidOrLogin.toLowerCase())) {
            // Используется для открытых пространств
            User usr = userIsOnline(moduleKey, BioServletBase.BIO_ANONYMOUS_USER_LOGIN);
            if(usr == null) {
                usr = new User();
                usr.setModuleKey(moduleKey);
                usr.setUid(BioServletBase.BIO_ANONYMOUS_USER_LOGIN);
                usr.setLogin(BioServletBase.BIO_ANONYMOUS_USER_LOGIN);
                usr.setFio("Anonymous User");
                usr.setRoles("*");
                usr.setGrants("*");
                storeUser(usr);
            }
            return usr;
        }
        return null;
    }

    @Override
    public User getUser(final String moduleKey, final String userUid) throws Exception {
        if(isNullOrEmpty(userUid))
            return null;

        User anonymouseUser = detectAnonymouse(moduleKey, userUid);
        if(anonymouseUser != null)
            return anonymouseUser;

        User onlineUser = userIsOnline(moduleKey, userUid);
        if(onlineUser != null){
            LOG.debug("User with uid \"{}\" alredy logged in as \"{}\".", userUid, onlineUser.getLogin());
            return onlineUser;
        } else
            throw new BioError.Login.LoginExpired();
    }

    @Override
    public User login(final String moduleKey, final String login) throws Exception {
        if(isNullOrEmpty(login))
            throw new BioError.Login.BadLogin();

        User anonymouseUser = detectAnonymouse(moduleKey, login);
        if(anonymouseUser != null)
            return anonymouseUser;

        if(ROOT_USER_LOGIN.equals(login.toLowerCase())) {
            // Встроенная учетка
            User usr = userIsOnline(moduleKey, ROOT_USER_UID);
            if(usr == null) {
                usr = new User();
                usr.setModuleKey(moduleKey);
                usr.setUid(ROOT_USER_UID);
                usr.setLogin("root");
                usr.setFio("Root User FIO");
                usr.setRoles("*");
                usr.setGrants("*");
                storeUser(usr);
            }
            return usr;
        }

        final BioModule module = moduleProvider.getModule(moduleKey);
        final BioModule bioModule = moduleProvider.getModule("bio");
        final BioCursor cursor = bioModule.getCursor("get-user");
        final SQLContext sqlContext = sqlContextProvider.selectContext(module);
        User newUsr = sqlContext.execBatch(new SQLAction<BioCursor, User>() {
            @Override
            public User exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                LOG.debug("User {} logging in...", login);
                cur.getSelectSqlDef().setParamValue("p_login", login);
                try(SQLCursor c = context.CreateCursor()
                        .init(conn, cur.getSelectSqlDef().getPreparedSql(), cur.getSelectSqlDef().getParams())
                        .open()) {
                    if (c.reader().next()){
                        User usr = new User();
                        usr.setModuleKey(moduleKey);
                        usr.setUid(c.reader().getValue("usr_uid", String.class));
                        usr.setLogin(c.reader().getValue("usr_login", String.class));
                        usr.setFio(c.reader().getValue("usr_fio", String.class));
                        usr.setRoles(c.reader().getValue("usr_roles", String.class));
                        usr.setGrants(c.reader().getValue("usr_grants", String.class));
                        LOG.debug("User found: {}", Utl.buildBeanStateInfo(usr, "User", "  "));
                        return usr;
                    }
                }
                LOG.debug("User not found!");
                return null;
            }
        }, cursor);
        if(newUsr == null)
            throw new BioError.Login.BadLogin();
        storeUser(newUsr);
        return newUsr;
    }

    @Override
    public void logoff(final String moduleKey, final String uid) throws Exception {
        removeUser(moduleKey, uid);
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
