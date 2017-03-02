package ru.bio4j.ng.service.types;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.bio4j.ng.commons.types.AnConfig;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
//import ru.bio4j.ng.database.oracle.SQLContextFactory;
//import ru.bio4j.ng.database.pgsql.SQLContextFactory;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioHttpRequestProcessor;
import ru.bio4j.ng.service.api.BioRouteHandler;
import ru.bio4j.ng.service.api.Configurator;
import ru.bio4j.ng.service.api.SrvcUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class BioModuleBase<T extends AnConfig> extends BioServiceBase<T> {
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
            try(InputStream inputStream = url.openStream()) {
                Document document = loadDocument(inputStream);
                cursor = CursorParser.pars(context, bioCode, document);
            }
        }
        return cursor;
    }

    protected abstract BundleContext bundleContext();

    protected static void applyCurrentUserParams(final User usr, final Collection<BioCursor.SQLDef> sqlDefs) {
        if (usr != null && sqlDefs != null) {
            for (BioCursor.SQLDef sqlDef : sqlDefs) {
                if (sqlDef != null)
                    try (Paramus p = Paramus.set(sqlDef.getParams())) {
                        p.setValue(SrvcUtils.PARAM_CURUSR_UID, usr.getInnerUid(), Param.Direction.IN, true);
                        p.setValue(SrvcUtils.PARAM_CURUSR_ORG_UID, usr.getOrgId(), Param.Direction.IN, true);
                        p.setValue(SrvcUtils.PARAM_CURUSR_ROLES, usr.getRoles(), Param.Direction.IN, true);
                        p.setValue(SrvcUtils.PARAM_CURUSR_GRANTS, usr.getGrants(), Param.Direction.IN, true);
                        p.setValue(SrvcUtils.PARAM_CURUSR_IP, usr.getRemoteIP(), Param.Direction.IN, true);
                    }
            }
        }
    }

    protected static void applyBioParams(final List<Param> bioParams, Collection<BioCursor.SQLDef> sqlDefs) {
        for(BioCursor.SQLDef sqlDef : sqlDefs) {
            if(sqlDef != null)
                sqlDef.setParams(bioParams);
        }
    }

    public BioCursor getCursor(String bioCode, User usr) throws Exception {
        BioCursor cursor = loadCursor(bundleContext(), bioCode);
        applyCurrentUserParams(usr, cursor.sqlDefs());
        if(cursor == null)
            throw new Exception(String.format("Cursor \"%s\" not found in module \"%s\"!", bioCode, this.getKey()));
        return cursor;
    }

    public BioCursor getCursor(String bioCode) throws Exception {
        return getCursor(bioCode, null);
    }

    public BioCursor getCursor(BioRequest bioRequest) throws Exception {
        String bioCode = bioRequest.getBioCode();
        BioCursor cursor = getCursor(bioCode, bioRequest.getUser());

        applyBioParams(bioRequest.getBioParams(), cursor.sqlDefs());

        return cursor;
    }

    private SQLContext sqlContext = null;
    private boolean localSQLContextIsInited = false;
    protected synchronized void initSqlContext(Configurator<T> configurator) throws Exception {
        if(sqlContext == null && !localSQLContextIsInited) {
            LOG.debug("Start initSqlContext for module \"{}\"...", this.getKey());
            localSQLContextIsInited = true;
            T cfg = configurator.getConfig();
            if(cfg == null)
                LOG.debug("Config not found for module \"{}\"!", this.getKey());
            else
                LOG.debug("Config found for module \"{}\"!", this.getKey());
            if(cfg instanceof SQLContextConfig) {
                LOG.debug("Try to create SQLContext for module \"{}\"...", this.getKey());
                sqlContext = createSQLContext((SQLContextConfig) cfg);
                if(sqlContext != null)
                    LOG.debug("SQLContext for module \"{}\" CREATED!", this.getKey());
                else
                    LOG.debug("SQLContext for module \"{}\" NOT CREATED!", this.getKey());
            }
            LOG.debug("Exit initSqlContext for module \"{}\".", this.getKey());
        }
    }

    public SQLContext getSQLContext() throws Exception {
        initSqlContext(getConfigurator());
        return sqlContext;
    }

    protected abstract SQLContext createSQLContext(SQLContextConfig config) throws Exception;

    protected abstract EventAdmin getEventAdmin();

    public abstract String getKey();

    protected void fireEventModuleUpdated() throws Exception {
        // Откладываем отправку события чтобы успел инициализироваться логгер
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(() -> {
            String selfModuleKey = getKey();
            LOG.debug("Sending event [bio-module-updated] for module \"{}\"...", selfModuleKey);
            Map<String, Object> props = new HashMap<>();
            props.put("bioModuleKey", selfModuleKey);
            getEventAdmin().postEvent(new Event("bio-module-updated", props));
            LOG.debug("Event sent.");
        }, 1, TimeUnit.SECONDS);

    }

    protected void fireEventModuleStarted() throws Exception {
        // Откладываем отправку события чтобы успел инициализироваться логгер
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(() -> {
            String selfModuleKey = this.getKey();
            LOG.debug("Sending event [bio-module-started] for module \"{}\"...", selfModuleKey);
            HashMap props = new HashMap();
            props.put("bioModuleKey", selfModuleKey);
            this.getEventAdmin().postEvent(new Event("bio-module-started", props));
            LOG.debug("Event sent.");
        }, 1L, TimeUnit.SECONDS);
    }

    //public abstract User login(final String login) throws Exception;

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

    public BioHttpRequestProcessor getHttpRequestProcessor(String requestType) {
        if(httpRequestProcessors.containsKey(requestType))
            return httpRequestProcessors.get(requestType);
        return null;
    }

    private Map<String, BioRouteHandler> routeMap;

    public BioRouteHandler getRouteHandler(String key) {
        if(routeMap != null && routeMap.containsKey(key))
            return routeMap.get(key);
        return null;
    }

    protected void registerRouteHandler(String key, BioRouteHandler routeHandler) {
        if(routeMap == null)
            routeMap = new HashMap<>();
        if(routeMap.containsKey(key))
            throw new IllegalArgumentException(String.format("%s with key \"%s\" already registered!", BioRouteHandler.class.getSimpleName(), key));
        routeMap.put(key, routeHandler);
    }

    protected void unregisterRouteHandler(String key) {
        if(routeMap != null && routeMap.containsKey(key))
            routeMap.remove(key);
    }
}
