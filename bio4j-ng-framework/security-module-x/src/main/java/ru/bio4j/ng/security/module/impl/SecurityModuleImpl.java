package ru.bio4j.ng.security.module.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioSecurityModule;
import ru.bio4j.ng.service.api.SecurityProvider;
import ru.bio4j.ng.service.types.BioModuleBase;

import java.sql.Connection;
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
    @Requires
    private SecurityProvider securityProvider;

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

    protected SQLContext createSQLContext(SQLContextConfig config) throws Exception {
            return ru.bio4j.ng.database.pgsql.SQLContextFactory.create(config);
    }

    @Override
    public User login(final String login, final String remoteIP) throws Exception {
        if (isNullOrEmpty(login))
            throw new BioError.Login.BadLogin();
        LOG.debug("User login:{} logging in...", login);

        final BioCursor cursor = this.getCursor("bio.login", null);
        final SQLContext context = this.getSQLContext();

        String stoken = context.execBatch(new SQLAction<BioCursor, String>() {
            @Override
            public String exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                cur.getExecSqlDef().setParamValue("p_login", login);
                SQLStoredProc sp = context.createStoredProc();
                sp.init(conn, cur.getExecSqlDef().getPreparedSql(), cur.getExecSqlDef().getParams())
                    .execSQL();
                try(Paramus paramus = Paramus.set(sp.getParams())) {
                    return paramus.getValueAsStringByName("v_security_token", true);
                }
            }
        }, cursor, null);

        if(stoken.equals("login-error-badlogin"))
            throw new BioError.Login.BadLogin();
        if(stoken.equals("login-error-disabled"))
            throw new BioError.Login.UserBlocked();
        if(stoken.equals("login-error-expired"))
            throw new BioError.Login.UserDeleted();

        return getUser(stoken, remoteIP);
    }

    @Override
    public User getUser(final String stoken, final String remoteIP) throws Exception {
        if (isNullOrEmpty(stoken))
            throw new BioError.Login.BadLogin();

        LOG.debug("User stoken:{} getting...", stoken);
        final BioCursor cursor = this.getCursor("bio.get-user", null);
        final SQLContext sqlContext = this.getSQLContext();
        User result = sqlContext.execBatch(new SQLAction<BioCursor, User>() {
            @Override
            public User exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                cur.getSelectSqlDef().setParamValue("p_security_token", stoken);
                try (SQLCursor c = context.createCursor()
                        .init(conn, cur.getSelectSqlDef().getPreparedSql(), cur.getSelectSqlDef().getParams())
                        .open()) {
                    if (c.reader().next()) {
                        User usr = new User();
                        usr.setInnerUid(c.reader().getValue("usr_uid", String.class));
                        usr.setStoken(c.reader().getValue("security_token", String.class));
                        usr.setLogin(c.reader().getValue("usr_login", String.class));
                        usr.setFio(c.reader().getValue("usr_fio", String.class));
                        usr.setEmail(c.reader().getValue("email_addr", String.class));
                        usr.setPhone(c.reader().getValue("usr_phone", String.class));
                        usr.setOrgId(c.reader().getValue("org_id", String.class));
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
                throw new BioError.Login.LoginExpired();
            }
        }, cursor, null);
        return result;
    }

    @Override
    public void logoff(final String stoken) throws Exception {
        final BioCursor cursor = this.getCursor("bio.logoff", null);
        final SQLContext context = this.getSQLContext();

        String rslt = context.execBatch(new SQLAction<BioCursor, String>() {
            @Override
            public String exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                cur.getExecSqlDef().setParamValue("p_stoken", stoken);
                SQLStoredProc sp = context.createStoredProc();
                sp.init(conn, cur.getExecSqlDef().getPreparedSql(), cur.getExecSqlDef().getParams())
                        .execSQL();
                try(Paramus paramus = Paramus.set(sp.getParams())) {
                    return paramus.getValueAsStringByName("v_rslt", true);
                }
            }
        }, cursor, null);
        LOG.debug("Logoff rslt: {}", rslt);
    }

    @Override
    public Boolean loggedin(final String stoken) throws Exception {
        final BioCursor cursor = this.getCursor("bio.loggedin", null);
        final SQLContext context = this.getSQLContext();

        String rslt = context.execBatch(new SQLAction<BioCursor, String>() {
            @Override
            public String exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                cur.getExecSqlDef().setParamValue("p_stoken", stoken);
                SQLStoredProc sp = context.createStoredProc();
                sp.init(conn, cur.getExecSqlDef().getPreparedSql(), cur.getExecSqlDef().getParams())
                        .execSQL();
                try(Paramus paramus = Paramus.set(sp.getParams())) {
                    return paramus.getValueAsStringByName("v_rslt", true);
                }
            }
        }, cursor, null);
        LOG.debug("Loggedin rslt: {}", rslt);
        return Strings.compare(rslt, "OK", true);
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
