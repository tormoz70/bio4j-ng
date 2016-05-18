package ru.bio4j.ng.security.module.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioSecurityModule;
import ru.bio4j.ng.service.types.BioModuleBase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Dictionary;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

@Component(managedservice="security.config")
@Instantiate
@Provides(specifications = BioSecurityModule.class,
        properties = {@StaticServiceProperty(
                name = "bioModuleKey",
                value = "security", // key must be always "security" for security module
                type = "java.lang.String"
        )})
public class SecurityModuleImpl extends BioModuleBase<SecurityConfig> implements BioSecurityModule {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityModuleImpl.class);

    @Requires
    private EventAdmin eventAdmin;

    @Override
    protected EventAdmin getEventAdmin() {
        return eventAdmin;
    }

    @Context
    private BundleContext bundleContext;

    @Override
    protected BundleContext bundleContext() {
        return bundleContext;
    }

    @Override
    public String getDescription() {
        return "Security module";
    }

    protected SQLContext createSQLContext(SQLContextConfig config) throws Exception {
            //return ru.bio4j.ng.database.oracle.SQLContextFactory.create(config);
            return ru.bio4j.ng.database.pgsql.SQLContextFactory.create(config);
    }

    private User _getUser(final String loginOrUid, final String remoteIP) throws Exception {
        if (isNullOrEmpty(loginOrUid))
            throw new BioError.Login.BadLogin();

        final String moduleKey = this.getKey();
        final BioCursor cursor = this.getCursor("bio.get-user");
        final SQLContext sqlContext = this.getSQLContext();
        try {
            User newUsr = sqlContext.execBatch(new SQLAction<BioCursor, User>() {
                @Override
                public User exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                    cur.getSelectSqlDef().setParamValue("p_login", loginOrUid);
                    try (SQLCursor c = context.createCursor()
                            .init(conn, cur.getSelectSqlDef().getPreparedSql(), cur.getSelectSqlDef().getParams())
                            .open()) {
                        if (c.reader().next()) {
                            User usr = new User();

                            usr.setUid(c.reader().getValue("usr_uid", String.class));
                            usr.setLogin(c.reader().getValue("usr_login", String.class));
                            usr.setFio(c.reader().getValue("usr_fio", String.class));
                            usr.setEmail(c.reader().getValue("email_addr", String.class));
                            usr.setPhone(c.reader().getValue("usr_phone", String.class));
                            usr.setOrgId(c.reader().getValue("org_uid", String.class));
                            usr.setOrgName(c.reader().getValue("org_name", String.class));
                            usr.setOrgDesc(c.reader().getValue("org_desc", String.class));
                            usr.setRoles(c.reader().getValue("usr_roles", String.class));
                            usr.setGrants(c.reader().getValue("usr_grants", String.class));
                            usr.setRemoteIP(remoteIP);
                            LOG.debug("User found: {}", Utl.buildBeanStateInfo(usr, "User", "  "));
                            return usr;
                        }
                    }
                    LOG.debug("User not found!");
                    return null;
                }
            }, cursor);
            return newUsr;
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case 20401:
                    throw new BioError.Login.BadLogin();
                case 20402:
                    throw new BioError.Login.UserBlocked();
                case 20403:
                    throw new BioError.Login.UserNotConfirmed();
                case 20404:
                    throw new BioError.Login.UserDeleted();
                default:
                    throw ex;
            }
        }
    }

    @Override
    public User login(final String login, final String remoteIP) throws Exception {
        if (isNullOrEmpty(login))
            throw new BioError.Login.BadLogin();
        LOG.debug("User {} logging in...", login);

        User usr = _getUser(login, remoteIP);
        return usr;
    }

    @Override
    public User getUser(final String uid, String remoteIP) throws Exception {

        if(User.BIO_ANONYMOUS_USER_LOGIN.equals(uid.toLowerCase())) {
            User usr = new User();
            usr.setUid(User.BIO_ANONYMOUS_USER_LOGIN);
            usr.setLogin(User.BIO_ANONYMOUS_USER_LOGIN);
            usr.setFio("Anonymous User");
            usr.setRoles("*");
            usr.setGrants("*");
            return usr;
        }

        LOG.debug("User {} getting...", uid);

        User usr = _getUser(uid, remoteIP);
        return usr;
    }

    @Override
    public void logoff(final String uid) throws Exception {
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

}
