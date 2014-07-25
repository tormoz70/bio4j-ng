package ru.bio4j.ng.crudhandlers.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.Wrappers;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


@Component
@Instantiate
@Provides(specifications = DataProvider.class)
public class DataProviderImpl extends BioServiceBase implements DataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DataProviderImpl.class);

    @Context
    private BundleContext bundleContext;
//    private SQLContext globalSQLContext;

    @Requires
    private SecurityHandler securityHandler;
    @Requires
    private ModuleProvider moduleProvider;
    @Requires
    private SQLContextProvider sqlContextProvider;

    private static void tryPrepareSessionContext(final String usrUID, final Connection conn) throws Exception {
        LOG.debug("Try setting session context...");
        try {
            CallableStatement cs = conn.prepareCall("{call biocontext.set_current_user(:usr_uid)}");
            cs.setString("usr_uid", usrUID);
            cs.execute();
            LOG.debug("Session context - OK.");
        } catch (Exception e) {
            LOG.debug("Session context - ERROR. Message: {}", e.getMessage());
        }

    }

    private static final String PARAM_CURUSR_UID =    "SYS_CURUSR_UID";
    private static final String PARAM_CURUSR_ROLES =  "SYS_CURUSR_ROLES";
    private static final String PARAM_CURUSR_GRANTS = "SYS_CURUSR_GRANTS";
    private static void applyCurrentUserParams(User usr, BioCursor cursor) {
        try(Paramus p = Paramus.set(cursor.getParams())) {
            p.setValue(PARAM_CURUSR_UID, usr.getUid(), false);
            p.setValue(PARAM_CURUSR_ROLES, usr.getRoles(), false);
            p.setValue(PARAM_CURUSR_GRANTS, usr.getGrants(), false);
        }
    }



    private BioRespBuilder.Data processCursorAsSelectable(BioRequest request, BioModule module, BioCursor cursor) throws Exception {
        final User usr = request.getUser();
        applyCurrentUserParams(usr, cursor);
        LOG.debug("Try exec batch!!!");
        SQLContext ctx = sqlContextProvider.selectContext(module);
        BioRespBuilder.Data response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.Data>() {
            @Override
            public BioRespBuilder.Data exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                tryPrepareSessionContext(usr.getUid(), conn);
                final BioRespBuilder.Data result = BioRespBuilder.data();
                result.bioCode(cur.getBioCode());
                LOG.debug("Try open Cursor!!!");
                try(SQLCursor c = context.CreateCursor()
                        .init(conn, cur.getPreparedSql(), cur.getParams()).open();) {
                    LOG.debug("Cursor opened!!!");
                    StoreData data = new StoreData();
                    data.setMetadata(new StoreMetadata());
                    data.getMetadata().setColumns(cur.getColumns());
                    List<StoreRow> rows = new ArrayList<>();
                    if(c.reader().read()) {
                        LOG.debug("FirstRec readed!!!");
                        StoreRow r = new StoreRow();
                        r.setValues(c.reader().getValues());
                        rows.add(r);
                    }
                    data.setRows(rows);
                    result.packet(data);
                }
                return result.success(true);
            }
        }, cursor);
        return response;
    }

    private BioRespBuilder.Data processCursorAsExecutable(BioRequest request, BioModule module, BioCursor cursor) {
        return BioRespBuilder.data();
    }

    public BioRespBuilder.Data processCursor(BioRequest request, BioModule module, BioCursor cursor) throws Exception {
        if(cursor.getType() == BioCursor.Type.SELECT)
            return processCursorAsSelectable(request, module, cursor);
        if(cursor.getType() == BioCursor.Type.EXEC)
            return processCursorAsExecutable(request, module, cursor);
        return BioRespBuilder.data();
    }

    private void wrapCursor(BioCursor cursor) throws Exception {
        Wrappers.wrapRequest(cursor, WrapQueryType.FILTERING);
        Wrappers.wrapRequest(cursor, WrapQueryType.SORTING);
        Wrappers.wrapRequest(cursor, WrapQueryType.PAGING);
    }

    private BioRespBuilder.Data processRequest(BioRequestJStoreGet request) throws Exception {
        String moduleKey = Utl.extractModuleKey(request.getBioCode());
        LOG.debug("Now processing request to module \"{}\"...", moduleKey);
        BioModule module = moduleProvider.getModule(moduleKey);
        BioCursor cursor = module.getCursor(request);
        wrapCursor(cursor);
        return processCursor(request, module, cursor);
    }

    @Override
    public BioRespBuilder.Data getData(final BioRequestJStoreGet bioRequest) throws Exception {
        LOG.debug("GetData...");
        try {
            return processRequest(bioRequest);
        } finally {
            LOG.debug("GetData - returning response...");
        }
    }

    @Override
    public String getDataTest() throws Exception {
        SQLContext globalSQLContext = sqlContextProvider.globalContext();
        return globalSQLContext.execBatch(new SQLActionScalar<String>() {
            @Override
            public String exec(SQLContext context, Connection conn) throws Exception {
                StringBuilder rslt = new StringBuilder();
                LOG.debug("Opening cursor...");
                try(SQLCursor c = context.CreateCursor()
                    .init(conn, "select username from user_users", null)
                    .open()) {
                    LOG.debug("Cursor opened...");
                    while (c.reader().read()){
                        LOG.debug("Reading field USERNAME...");
                        String s = c.reader().getValue("USERNAME", String.class);
                        rslt.append(s+";");
                    }
                }
                return rslt.toString();
            }
        });
    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");
        Wrappers.getInstance().init("oracle");
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
        doStop();
        doStart();
    }

}
