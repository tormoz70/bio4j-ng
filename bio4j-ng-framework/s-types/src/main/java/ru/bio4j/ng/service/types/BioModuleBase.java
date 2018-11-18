package ru.bio4j.ng.service.types;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.StoredProgMetadata;
import ru.bio4j.ng.service.api.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class BioModuleBase<T extends AnConfig> extends BioServiceBase<T> {
    private static final Logger LOG = LoggerFactory.getLogger(BioModuleBase.class);

    protected abstract BundleContext bundleContext();

//    protected static void applyCurrentUserParams(final User usr, final Collection<BioCursorDeclaration.SQLDef> sqlDefs) {
//        if (usr != null && sqlDefs != null) {
//            for (BioCursorDeclaration.SQLDef sqlDef : sqlDefs) {
//                if (sqlDef != null)
//                    try (Paramus p = Paramus.set(sqlDef.getParamDeclaration())) {
//                        p.setValue(SrvcUtils.PARAM_CURUSR_UID, usr.getInnerUid(), Param.Direction.IN, true);
//                        p.setValue(SrvcUtils.PARAM_CURUSR_ORG_UID, usr.getOrgId(), Param.Direction.IN, true);
//                        p.setValue(SrvcUtils.PARAM_CURUSR_ROLES, usr.getRoles(), Param.Direction.IN, true);
//                        p.setValue(SrvcUtils.PARAM_CURUSR_GRANTS, usr.getGrants(), Param.Direction.IN, true);
//                        p.setValue(SrvcUtils.PARAM_CURUSR_IP, usr.getRemoteIP(), Param.Direction.IN, true);
//                        p.setValue(SrvcUtils.PARAM_CURUSR_CLIENT, usr.getRemoteClient(), Param.Direction.IN, true);
//                    }
//            }
//        }
//    }

//    protected static void applyBioParams(final List<Param> bioParams, Collection<BioCursorDeclaration.SQLDef> sqlDefs) throws Exception {
//        for(BioCursorDeclaration.SQLDef sqlDef : sqlDefs) {
//            if(sqlDef != null)
//                sqlDef.setParams(bioParams);
//        }
//    }

    private void prepareCursor(BioCursorDeclaration cursor) throws Exception {
        SQLContext context = this.getSQLContext();
        context.execBatch((ctx) -> {
            UpdelexSQLDef def = cursor.getUpdateSqlDef();
            if (def != null) {
                StoredProgMetadata sp = ctx.prepareStoredProc(def.getPreparedSql(), ctx.getCurrentConnection(), def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
            def = cursor.getDeleteSqlDef();
            if (def != null) {
                StoredProgMetadata sp = ctx.prepareStoredProc(def.getPreparedSql(), ctx.getCurrentConnection(), def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
            def = cursor.getExecSqlDef();
            if (def != null) {
                StoredProgMetadata sp = ctx.prepareStoredProc(def.getPreparedSql(), ctx.getCurrentConnection(), def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
        }, null);
    }

    public BioCursorDeclaration getCursor(String bioCode) throws Exception {
        BioCursorDeclaration cursor = CursorParser.pars(bundleContext(), bioCode);
        if(cursor == null)
            throw new Exception(String.format("Cursor \"%s\" not found in module \"%s\"!", bioCode, this.getKey()));
        prepareCursor(cursor);
//        applyCurrentUserParams(usr, cursor.sqlDefs());
        return cursor;
    }

//    public BioCursorDeclaration getCursor(String bioCode) throws Exception {
//        return getCursor(bioCode, null);
//    }

//    public BioCursorDeclaration getCursor(BioRequest bioRequest) throws Exception {
//        String bioCode = bioRequest.getBioCode();
//        BioCursorDeclaration cursor = getCursor(bioCode, bioRequest.getUser());
//
//        //applyBioParams(bioRequest.getBioParams(), cursor.sqlDefs());
//
//        return cursor;
//    }

    private SQLContext sqlContext = null;
    private boolean localSQLContextIsInited = false;
    protected synchronized void initSqlContext() throws Exception {
        if(sqlContext == null && !localSQLContextIsInited) {
            LOG.debug("Start initSqlContext for module \"{}\"...", this.getKey());
            localSQLContextIsInited = true;
            LOG.debug("Try to create SQLContext for module \"{}\"...", this.getKey());
            sqlContext = createSQLContext();
            if(sqlContext != null)
                LOG.debug("SQLContext for module \"{}\" CREATED!", this.getKey());
            else
                LOG.debug("SQLContext for module \"{}\" NOT CREATED!", this.getKey());
            LOG.debug("Exit initSqlContext for module \"{}\".", this.getKey());
        }
    }

    public SQLContext getSQLContext() throws Exception {
        initSqlContext();
        return sqlContext;
    }

    protected abstract SQLContext createSQLContext() throws Exception;

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
