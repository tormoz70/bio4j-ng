package ru.bio4j.ng.module.commons;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
//import ru.bio4j.ng.database.oracle.SQLContextFactory;
//import ru.bio4j.ng.database.pgsql.SQLContextFactory;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioHttpRequestProcessor;
import ru.bio4j.ng.service.api.BioModule;
import ru.bio4j.ng.service.api.Configurator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public abstract class BioModuleBase implements BioModule {
    private static final Logger LOG = LoggerFactory.getLogger(BioModuleBase.class);

    private static Document loadDocument(InputStream inputStream) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        DocumentBuilder builder = f.newDocumentBuilder();
        return builder.parse(inputStream);
    }


    private static BioCursor loadCursor(BundleContext context, String bioCode) throws Exception {
        BioCursor cursor = null;
        String path = Utl.extractBioPath(bioCode);
        URL url = context.getBundle().getResource(path + ".xml");
        if(url != null) {
            LOG.debug("Loading cursor spec from \"{}\"", path + ".xml");
            InputStream inputStream = url.openStream();
            Document document = loadDocument(inputStream);
            cursor = CursorParser.pars(bioCode, document);
        }
        return cursor;
    }

    protected abstract BundleContext bundleContext();

    @Override
    public BioCursor getCursor(String bioCode) throws Exception {
        BioCursor cursor = loadCursor(bundleContext(), bioCode);
        if(cursor == null)
            throw new Exception(String.format("Cursor \"%s\" not found in module \"%s\"!", bioCode, this.getSelfModuleKey()));
        return cursor;
    }

    private static final String SQL_CONTEXT_CONFIG_FILE_NAME = "sql-context.config";
    private Configurator<SQLContextConfig> configurator = new Configurator<>(SQLContextConfig.class);
    private SQLContext sqlContext = null;
    private boolean localSQLContextIsInited = false;
    public SQLContext getSQLContext() throws Exception {
        if(sqlContext == null && !localSQLContextIsInited) {
            localSQLContextIsInited = true;
            BundleContext bundleContext = bundleContext();
            String selfModuleKey = getSelfModuleKey();
            LOG.debug("Getting SQLContext for module \"{}\"...", selfModuleKey);
            URL url = bundleContext.getBundle().getResource(SQL_CONTEXT_CONFIG_FILE_NAME);
            if (url != null) {
                InputStream inputStream = url.openStream();
                if (inputStream != null) {
                    configurator.load(inputStream);
                    sqlContext = createSQLContext(configurator.getConfig()); //SQLContextFactory.create(configurator.getConfig());
                    LOG.debug("Context description for module \"{}\" loaded from file \"{}\".", SQL_CONTEXT_CONFIG_FILE_NAME, selfModuleKey);
                }
            } else
                LOG.debug("File \"{}\" not found for module \"{}\"...", SQL_CONTEXT_CONFIG_FILE_NAME, selfModuleKey);
        }
        return sqlContext;
    }

    protected abstract SQLContext createSQLContext(SQLContextConfig config) throws Exception;

    protected abstract EventAdmin getEventAdmin();

    protected abstract String getSelfModuleKey();

    protected void fireEventModuleUpdated() throws Exception {
        // Откладываем отправку события чтобы успел инициализироваться логгер
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(new Runnable() {
            @Override
            public void run() {
            String selfModuleKey = getSelfModuleKey();
            LOG.debug("Sending event [bio-module-updated] for module \"{}\"...", selfModuleKey);
            Map<String, Object> props = new HashMap<>();
            props.put("bioModuleKey", selfModuleKey);
            getEventAdmin().postEvent(new Event("bio-module-updated", props));
            LOG.debug("Event sent.");
            }
        }, 1, TimeUnit.SECONDS);

    }

    @Override
    public User login(final String login) throws Exception {
        if(isNullOrEmpty(login))
            throw new BioError.Login.BadLogin();

        final String moduleKey = this.getSelfModuleKey();
        final BioCursor cursor = this.getCursor("bio.get-user");
        final SQLContext sqlContext = this.getSQLContext();
        User newUsr = sqlContext.execBatch(new SQLAction<BioCursor, User>() {
            @Override
            public User exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                LOG.debug("User {} logging in...", login);
                cur.getSelectSqlDef().setParamValue("p_login", login);
                try(SQLCursor c = context.createCursor()
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
        return newUsr;
    }

    private final Map<String, BioHttpRequestProcessor> httpRequestProcessors = new HashMap<>();
    protected void registerHttpRequestProcessor(String requestType, BioHttpRequestProcessor processor) {
        if(httpRequestProcessors.containsKey(requestType))
            throw new IllegalArgumentException(String.format("%s with key \"%s\" already registered!", BioHttpRequestProcessor.class.getSimpleName(), requestType));
        httpRequestProcessors.put(requestType, processor);
    }
    protected void unregisterHttpRequestProcessor(String requestType) {
        if(httpRequestProcessors.containsKey(requestType))
            httpRequestProcessors.remove(requestType);
    }

    @Override
    public BioHttpRequestProcessor getHttpRequestProcessor(String requestType) {
        if(httpRequestProcessors.containsKey(requestType))
            return httpRequestProcessors.get(requestType);
        return null;
    }
}
