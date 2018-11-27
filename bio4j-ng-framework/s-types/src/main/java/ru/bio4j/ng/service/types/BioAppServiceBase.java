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

public abstract class BioAppServiceBase<T extends AnConfig> extends BioServiceBase<T> {
    private static final Logger LOG = LoggerFactory.getLogger(BioAppServiceBase.class);

    protected abstract BundleContext bundleContext();

    private void prepareSQL(BioSQLDefinitionImpl sqlDefinition) throws Exception {
        SQLContext context = this.getSQLContext();
        context.execBatch((ctx) -> {
            UpdelexSQLDef def = sqlDefinition.getUpdateSqlDef();
            if (def != null) {
                StoredProgMetadata sp = ctx.prepareStoredProc(def.getPreparedSql(), ctx.getCurrentConnection(), def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
            def = sqlDefinition.getDeleteSqlDef();
            if (def != null) {
                StoredProgMetadata sp = ctx.prepareStoredProc(def.getPreparedSql(), ctx.getCurrentConnection(), def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
            def = sqlDefinition.getExecSqlDef();
            if (def != null) {
                StoredProgMetadata sp = ctx.prepareStoredProc(def.getPreparedSql(), ctx.getCurrentConnection(), def.getParamDeclaration());
                def.setSignature(sp.getSignature());
                def.setParamDeclaration(sp.getParamDeclaration());
            }
        }, null);
    }

    public BioSQLDefinitionImpl getSQLDefinition(String bioCode) throws Exception {
        BioSQLDefinitionImpl cursor = CursorParser.pars(bundleContext(), bioCode);
        if(cursor == null)
            throw new Exception(String.format("Cursor \"%s\" not found in service \"%s\"!", bioCode, this.getClass().getName()));
        prepareSQL(cursor);
        return cursor;
    }

    private SQLContext sqlContext = null;
    private boolean localSQLContextIsInited = false;
    protected synchronized void initSqlContext() throws Exception {
        if(sqlContext == null && !localSQLContextIsInited) {
            LOG.debug("Start initSqlContext for service \"{}\"...", this.getClass().getName());
            localSQLContextIsInited = true;
            LOG.debug("Try to create SQLContext for service \"{}\"...", this.getClass().getName());
            sqlContext = createSQLContext();
            if(sqlContext != null)
                LOG.debug("SQLContext for service \"{}\" CREATED!", this.getClass().getName());
            else
                LOG.debug("SQLContext for service \"{}\" NOT CREATED!", this.getClass().getName());
            LOG.debug("Exit initSqlContext for service \"{}\".", this.getClass().getName());
        }
    }

    public SQLContext getSQLContext() throws Exception {
        initSqlContext();
        return sqlContext;
    }

    protected abstract SQLContext createSQLContext() throws Exception;

    protected abstract EventAdmin getEventAdmin();

    protected void fireEventModuleUpdated() throws Exception {
        // Откладываем отправку события чтобы успел инициализироваться логгер
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(() -> {
            LOG.debug("Sending event [bio-service-updated] for service \"{}\"...", this.getClass().getName());
            Map<String, Object> props = new HashMap<>();
            getEventAdmin().postEvent(new Event("bio-module-updated", props));
            LOG.debug("Event sent.");
        }, 1, TimeUnit.SECONDS);

    }

    protected void fireEventModuleStarted() throws Exception {
        // Откладываем отправку события чтобы успел инициализироваться логгер
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(() -> {
            LOG.debug("Sending event [bio-service-started] for service \"{}\"...", this.getClass().getName());
            HashMap props = new HashMap();
            this.getEventAdmin().postEvent(new Event("bio-service-started", props));
            LOG.debug("Event sent.");
        }, 1L, TimeUnit.SECONDS);
    }

}
