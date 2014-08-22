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
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.filtering.GetrowWrapper;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.pagination.LocateWrapper;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.pagination.PaginationWrapper;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.Param;
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

    private static final int MAX_RECORDS_FETCH_LIMIT = 500;

    private BioRespBuilder.Data processCursorAsSelectableWithPagging(final User usr, final BioRequestJStoreGet request, BioModule module, BioCursor cursor) throws Exception {
        LOG.debug("Try open Cursor as MultiPage!!!");
        final SQLContext ctx = sqlContextProvider.selectContext(module);
        final BioRespBuilder.Data response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.Data>() {
            @Override
            public BioRespBuilder.Data exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                tryPrepareSessionContext(usr.getUid(), conn);
                final BioRespBuilder.Data result = BioRespBuilder.data().success(true);
                result.bioCode(cur.getBioCode());
                int totalCount = request.getTotalCount();
                if(totalCount == 0) {
                    LOG.debug("Try calc count of cursor records!!!");
                    try (SQLCursor c = context.CreateCursor()
                            .init(conn, cur.getTotalsSql(), cur.getParams()).open();) {
                        if (c.reader().next())
                            totalCount = c.reader().getValue(1, int.class);
                    }
                    LOG.debug("Count of cursor records - {}!!!", totalCount);
                }

                if(cur.getLocation() != null) {
                    LOG.debug("Try locate cursor to [{}] record by pk!!!", cur.getLocation());
                    List<Param> locateParams = Paramus.clone(cur.getParams());
                    try(Paramus p = Paramus.set(locateParams)){
                        p.setValue(LocateWrapper.PKVAL, cur.getLocation());
                        p.setValue(LocateWrapper.STARTFROM, cur.getOffset());
                    }
                    try (SQLCursor c = context.CreateCursor()
                            .init(conn, cur.getLocateSql(), locateParams).open();) {
                        if (c.reader().next()) {
                            int locatedPos = c.reader().getValue(1, int.class);
                            int offset = (locatedPos / cur.getPageSize()) * cur.getPageSize();
                            LOG.debug("Cursor successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cur.getLocation(), locatedPos, offset);
                            cur.setOffset(offset);
                            cur.setParamValue(PaginationWrapper.OFFSET, cur.getOffset())
                                    .setParamValue(PaginationWrapper.LAST, cur.getOffset() + cur.getPageSize());
                        } else {
                            LOG.debug("Cursor fail location to [{}] record by pk!!!", cur.getLocation());
                            result.success(false);
                            result.addError(new BioError.LacationFail(cur.getLocation()));
                        }
                    }
                }

                LOG.debug("Opening Cursor...");
                try(SQLCursor c = context.CreateCursor()
                        .init(conn, cur.getPreparedSql(), cur.getParams()).open();) {
                    LOG.debug("Cursor opened!!!");
                    StoreData data = new StoreData();
                    data.setOffset(cur.getOffset());
                    data.setPageSize(cur.getPageSize());
                    data.setMetadata(new StoreMetadata());
                    data.setResults(totalCount);
                    List<Column> cols = cur.getColumns();
                    data.getMetadata().setColumns(cols);
                    List<StoreRow> rows = new ArrayList<>();
                    while(c.reader().next()) {
                        StoreRow r = new StoreRow();
                        List<Object> vals = new ArrayList<>();
                        for (Column col : cols) {
                            Field f = c.reader().getField(col.getName());
                            if (f != null)
                                vals.add(c.reader().getValue(f.getId()));
                            else
                                vals.add(null);
                        }
                        r.setValues(vals);
                        rows.add(r);
                    }
                    data.getMetadata().setColumns(cols);
                    data.setRows(rows);
                    result.packet(data);
                }
                return result;
            }
        }, cursor);
        return response;
    }

    private BioRespBuilder.Data processCursorAsSelectableSinglePage(final User usr, final BioRequestJStoreGet request, BioModule module, BioCursor cursor) throws Exception {
        LOG.debug("Try open Cursor as SinglePage!!!");
        SQLContext ctx = sqlContextProvider.selectContext(module);
        BioRespBuilder.Data response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.Data>() {
            @Override
            public BioRespBuilder.Data exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                tryPrepareSessionContext(usr.getUid(), conn);
                final BioRespBuilder.Data result = BioRespBuilder.data();
                result.bioCode(cur.getBioCode());

                String preparedSQL = cur.getPreparedSql();
                if(request.getId() != null) {
                    preparedSQL = cur.getGetrowSql();
                    cur.setParamValue(GetrowWrapper.PKVAL, request.getId());
                }

                LOG.debug("Opening Cursor...");
                int totalCount = 0;
                try(SQLCursor c = context.CreateCursor()
                        .init(conn, preparedSQL, cur.getParams()).open();) {
                    LOG.debug("Cursor opened!!!");
                    StoreData data = new StoreData();
                    data.setOffset(cur.getOffset());
                    data.setPageSize(cur.getPageSize());
                    data.setMetadata(new StoreMetadata());
                    List<Column> cols = cur.getColumns();
                    data.getMetadata().setColumns(cols);
                    List<StoreRow> rows = new ArrayList<>();
                    while(c.reader().next()) {
                        StoreRow r = new StoreRow();
                        List<Object> vals = new ArrayList<>();
                        for (Column col : cols) {
                            Field f = c.reader().getField(col.getName());
                            if (f != null)
                                vals.add(c.reader().getValue(f.getId()));
                            else
                                vals.add(null);
                        }
                        r.setValues(vals);
                        rows.add(r);
                        totalCount = rows.size();
                        if(totalCount >= MAX_RECORDS_FETCH_LIMIT) {
                            totalCount = 0;
                            break;
                        }
                    }
                    data.getMetadata().setColumns(cols);
                    data.setRows(rows);

                    result.packet(data);
                }

                if(totalCount == 0) {
                    LOG.debug("Max records fetched [{}]! Try calc count of total cursor records!!!", MAX_RECORDS_FETCH_LIMIT);
                    try (SQLCursor c = context.CreateCursor()
                            .init(conn, cur.getTotalsSql(), cur.getParams()).open();) {
                        if (c.reader().next())
                            totalCount = c.reader().getValue(1, int.class);
                    }
                    LOG.debug("Count of total cursor records - {}!!!", totalCount);
                }
                result.getPacket().setResults(totalCount);
                return result.success(true);
            }
        }, cursor);
        return response;

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

    private BioRespBuilder.Data processCursorAsSelectable(final BioRequestJStoreGet request, BioModule module, BioCursor cursor) throws Exception {
        final User usr = request.getUser();
        applyCurrentUserParams(usr, cursor);
        if(cursor.getPageSize() < 0 || request.getId() != null)
            return processCursorAsSelectableSinglePage(usr, request, module, cursor);
        else
            return processCursorAsSelectableWithPagging(usr, request, module, cursor);

    }

    private BioRespBuilder.Data processCursorAsExecutable(BioRequest request, BioModule module, BioCursor cursor) {
        return BioRespBuilder.data();
    }

    public BioRespBuilder.Data processCursor(BioRequestJStoreGet request, BioModule module, BioCursor cursor) throws Exception {
        if(cursor.getType() == BioCursor.Type.SELECT) {
            wrapCursor(cursor);
            return processCursorAsSelectable(request, module, cursor);
        }
        if(cursor.getType() == BioCursor.Type.EXEC)
            return processCursorAsExecutable(request, module, cursor);
        return BioRespBuilder.data();
    }

    private void wrapCursor(BioCursor cursor) throws Exception {
        Wrappers.wrapRequest(cursor, WrapQueryType.FILTERING);
        Wrappers.wrapRequest(cursor, WrapQueryType.TOTALS);
        Wrappers.wrapRequest(cursor, WrapQueryType.SORTING);
        Wrappers.wrapRequest(cursor, WrapQueryType.LOCATE);
        Wrappers.wrapRequest(cursor, WrapQueryType.PAGING);
        Wrappers.wrapRequest(cursor, WrapQueryType.GETROW);
    }

    private BioRespBuilder.Data processRequest(BioRequestJStoreGet request) throws Exception {
        String moduleKey = Utl.extractModuleKey(request.getBioCode());
        LOG.debug("Now processing request to module \"{}\"...", moduleKey);
        BioModule module = moduleProvider.getModule(moduleKey);
        BioCursor cursor = module.getCursor(request);
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
                while (c.reader().next()){
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
