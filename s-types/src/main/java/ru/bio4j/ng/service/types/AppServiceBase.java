package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.StoredProgMetadata;
import ru.bio4j.ng.model.transport.AnConfig;
import ru.bio4j.ng.service.api.*;

public abstract class AppServiceBase<T extends AnConfig> extends ServiceBase<T> {
    private static final Logger LOG = LoggerFactory.getLogger(AppServiceBase.class);

    private void prepareSQL(SQLDefinitionImpl sqlDefinition) throws Exception {
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

    public SQLDefinitionImpl getSQLDefinition(String bioCode) throws Exception {
        SQLDefinitionImpl cursor = CursorParser.pars(bundleContext(), bioCode);
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

    public HttpParamMap createHttpParamMap() throws Exception {
        return null;
    }

    public LoginProcessor createLoginProcessor() {
        return null;
    }
}
