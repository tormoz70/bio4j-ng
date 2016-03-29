package ru.bio4j.ng.service.types;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
//import ru.bio4j.ng.database.oracle.SQLContextFactory;
//import ru.bio4j.ng.database.pgsql.SQLContextFactory;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioHttpRequestProcessor;
import ru.bio4j.ng.service.api.Configurator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public abstract class BioModuleBase<T extends SQLContextConfig> extends BioServiceBase<T> {
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

    protected static void applyCurrentUserParams(final User usr, Collection<BioCursor.SQLDef> sqlDefs) {
        for(BioCursor.SQLDef sqlDef : sqlDefs) {
            if(sqlDef != null)
                try (Paramus p = Paramus.set(sqlDef.getParams())) {
                    p.setValue(SrvcUtils.PARAM_CURUSR_UID, usr.getUid(), true);
                    p.setValue(SrvcUtils.PARAM_CURUSR_ROLES, usr.getRoles(), true);
                    p.setValue(SrvcUtils.PARAM_CURUSR_GRANTS, usr.getGrants(), true);
                }
        }
    }

    public BioCursor getCursor(String bioCode) throws Exception {
        BioCursor cursor = loadCursor(bundleContext(), bioCode);
        if(cursor == null)
            throw new Exception(String.format("Cursor \"%s\" not found in module \"%s\"!", bioCode, this.getKey()));
        return cursor;
    }

    public BioCursor getCursor(BioRequest bioRequest) throws Exception {
        String bioCode = bioRequest.getBioCode();
        BioCursor cursor = getCursor(bioCode);

        final User usr = bioRequest.getUser();
        applyCurrentUserParams(usr, cursor.sqlDefs());

        return cursor;
    }

    private SQLContext sqlContext = null;
    private boolean localSQLContextIsInited = false;
    protected void initSqlContext(Configurator<T> configurator) throws Exception {
        if(sqlContext == null && !localSQLContextIsInited) {
            localSQLContextIsInited = true;
            sqlContext = createSQLContext(configurator.getConfig());
        }

    }

    public SQLContext getSQLContext() throws Exception {
        initSqlContext(getConfigurator());
        return sqlContext;
    }

    protected abstract SQLContext createSQLContext(SQLContextConfig config) throws Exception;

    protected abstract EventAdmin getEventAdmin();

    private String _bioModuleKey;

    public String getKey() {
        return _bioModuleKey;
    }

    public void setKey(String s) {
        _bioModuleKey = s.toLowerCase();
    }

    protected void fireEventModuleUpdated() throws Exception {
        // Откладываем отправку события чтобы успел инициализироваться логгер
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(new Runnable() {
            @Override
            public void run() {
            String selfModuleKey = getKey();
            LOG.debug("Sending event [bio-module-updated] for module \"{}\"...", selfModuleKey);
            Map<String, Object> props = new HashMap<>();
            props.put("bioModuleKey", selfModuleKey);
            getEventAdmin().postEvent(new Event("bio-module-updated", props));
            LOG.debug("Event sent.");
            }
        }, 1, TimeUnit.SECONDS);

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
}
