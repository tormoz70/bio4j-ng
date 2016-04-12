package ru.bio4j.ng.security.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

@Component
@Instantiate
@Provides(specifications = SecurityProvider.class)
public class SecurityProviderImpl extends BioServiceBase implements SecurityProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityProviderImpl.class);

//    @Context
//    private BundleContext bundleContext;

    @Requires
    private ModuleProvider moduleProvider;
//    @Requires
//    private SQLContextProvider sqlContextProvider;

    private ConcurrentMap<String, Object> onlineUsers = new ConcurrentHashMap<>();

    private final String securityModuleKey = "security";

    private static final Object dummy = new Object();
    private void storeUser(final String userUid) throws Exception {
        Object existsUser = onlineUsers.get(userUid);
        if(existsUser != null) {
            return;
        }
        onlineUsers.put(String.format("%s-%s", securityModuleKey, userUid), dummy);
    }

    private Boolean userIsOnline(final String userUID) {
        return onlineUsers.get(String.format("%s-%s", securityModuleKey, userUID)) != null;
    }

    private void removeUser(final String userUID) {
        onlineUsers.remove(String.format("%s-%s", securityModuleKey, userUID));
    }

//    private static final String ROOT_USER_LOGIN = "root/root";
//    private static final String ROOT_USER_UID = "root-user-uid";

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

    private Boolean detectAnonymous(String userUid) throws Exception {
        if(User.BIO_ANONYMOUS_USER_LOGIN.equals(userUid.toLowerCase())) {
            // Используется для открытых пространств
            Boolean userIsOnline = userIsOnline(User.BIO_ANONYMOUS_USER_LOGIN);
            if(!userIsOnline) {
                storeUser(User.BIO_ANONYMOUS_USER_LOGIN);
            }
            return true;
        }
        return false;
    }

    @Override
    public User getUser(final String userUid) throws Exception {
        if(isNullOrEmpty(userUid))
            throw new BioError.Login.BadLogin();

        Boolean anonymousUserIsOk = detectAnonymous(userUid);
        if(anonymousUserIsOk) {
            LOG.debug("Anonymous User with uid \"{}\" logged in.", userUid);
            return getSecurityModule().getUser(userUid);
        }

        Boolean userIsOnline = userIsOnline(userUid);
        if(userIsOnline){
            LOG.debug("User with uid \"{}\" already logged in.", userUid);
            return getSecurityModule().getUser(userUid);
        } else
            throw new BioError.Login.LoginExpired();
    }

    @Override
    public User login(final String login) throws Exception {
        if(isNullOrEmpty(login))
            throw new BioError.Login.BadLogin();

        Boolean anonymousUserIsOk = detectAnonymous(login);
        if(anonymousUserIsOk) {
            LOG.debug("Anonymous User with login \"{}\" logged in.", login);
            return getSecurityModule().getUser(login);
        }

//        if(ROOT_USER_LOGIN.equals(login.toLowerCase())) {
//            // Встроенная учетка
//            User usr = userIsOnline(moduleKey, ROOT_USER_UID);
//            if(usr == null) {
//                usr = new User();
//                usr.setModuleKey(moduleKey);
//                usr.setUid(ROOT_USER_UID);
//                usr.setLogin("root");
//                usr.setFio("Root User FIO");
//                usr.setRoles("*");
//                usr.setGrants("*");
//                storeUser(usr);
//            }
//            return usr;
//        }

        User newUsr = getSecurityModule().login(login);
        if(newUsr == null)
            throw new BioError.Login.BadLogin();
        storeUser(newUsr.getUid());
        return newUsr;
    }

    @Override
    public void logoff(final String uid) throws Exception {
        removeUser(uid);
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