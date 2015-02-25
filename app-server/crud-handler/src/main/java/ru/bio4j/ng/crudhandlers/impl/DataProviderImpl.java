package ru.bio4j.ng.crudhandlers.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.types.TimedCache;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.commons.wrappers.filtering.GetrowWrapper;
import ru.bio4j.ng.database.commons.wrappers.pagination.LocateWrapper;
import ru.bio4j.ng.database.commons.wrappers.pagination.PaginationWrapper;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.*;


@Component
@Instantiate
@Provides(specifications = DataProvider.class)
public class DataProviderImpl extends BioServiceBase implements DataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DataProviderImpl.class);

    @Context
    private BundleContext bundleContext;

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

    private static String buildRequestKey(final BioRequestJStoreGetDataSet request) {
        String paramsUrl = null;
        List<Param> params = request.getBioParams();
        if (params != null) {
            try (Paramus p = Paramus.set(params)) {
                paramsUrl = p.buildUrlParams();
            }
        }
        return request.getBioCode()+(Strings.isNullOrEmpty(paramsUrl) ? "/<noparams>" : "/"+paramsUrl);
    }

    private static TimedCache<Integer> requestCache = new TimedCache(300);
    private static boolean requestCached(final BioRequestJStoreGetDataSet request) {
        String key = buildRequestKey(request);
        int check = Utl.nvl(requestCache.get(key), 0);
        requestCache.put(key, 1);
        LOG.debug("Processed requestCache, key: \"{}\" - check is [{}].", key, check);
        return check > 0;
    }

    public static int calcOffset(int locatedPos, int pageSize){
        int pg = ((int)((double)(locatedPos-1) / (double)pageSize) + 1);
        LOG.debug("pg: {}", pg);
        return (pg - 1) * pageSize;
    }

    private static int readStoreData(final StoreData data, final SQLContext context, final Connection conn, final BioCursor cursorDef) throws Exception {
        LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        int totalCount = 0;
        try(SQLCursor c = context.CreateCursor()
                .init(conn, cursorDef.getSelectSqlDef().getPreparedSql(), cursorDef.getSelectSqlDef().getParams()).open();) {
            LOG.debug("Cursor \"{}\" opened!!!", cursorDef.getBioCode());
            data.setMetadata(new StoreMetadata());
            List<Field> fields = cursorDef.getFields();
            data.getMetadata().setFields(fields);
            List<StoreRow> rows = new ArrayList<>();
            while(c.reader().next()) {
                StoreRow r = new StoreRow();
                Map<String, Object> vals = new HashMap<>();
                for (Field field : fields) {
                    DBField f = c.reader().getField(field.getName());
                    if (f != null)
                        vals.put(field.getName().toLowerCase(), c.reader().getValue(f.getId()));
                    else
                        vals.put(field.getName().toLowerCase(), null);
                }
                r.setData(vals);
                rows.add(r);
                totalCount = rows.size();
                if(totalCount >= MAX_RECORDS_FETCH_LIMIT) {
                    totalCount = 0;
                    break;
                }
            }
            LOG.debug("Cursor \"{}\" fetched! {} - records loaded.", cursorDef.getBioCode(), rows.size());
            data.setRows(rows);
        }
        return totalCount;
    }

    private BioRespBuilder.Data processCursorAsSelectableWithPagging(final User usr, final BioRequestJStoreGetDataSet request, final SQLContext ctx, final BioCursor cursor) throws Exception {
        LOG.debug("Try open Cursor \"{}\" as MultiPage!!!", cursor.getBioCode());
        final BioRespBuilder.Data response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.Data>() {
            @Override
            public BioRespBuilder.Data exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                tryPrepareSessionContext(usr.getUid(), conn);
                final BioRespBuilder.Data result = BioRespBuilder.data().exception(null);
                result.bioCode(cur.getBioCode());
                boolean requestCached = requestCached(request);

                int totalCount = requestCached ? request.getTotalCount() : 0;
                if(totalCount == 0) {
                    LOG.debug("Try calc count records of cursor \"{}\"!!!", cur.getBioCode());
                    try (SQLCursor c = context.CreateCursor()
                            .init(conn, cur.getSelectSqlDef().getTotalsSql(), cur.getSelectSqlDef().getParams()).open();) {
                        if (c.reader().next())
                            totalCount = c.reader().getValue(1, int.class);
                    }
                    LOG.debug("Count records of cursor \"{}\" - {}!!!", cur.getBioCode(), totalCount);
                }

                if(cur.getSelectSqlDef().getLocation() != null) {
                    LOG.debug("Try locate cursor \"{}\" to [{}] record by pk!!!", cur.getBioCode(), cur.getSelectSqlDef().getLocation());
                    List<Param> locateParams = Paramus.clone(cur.getSelectSqlDef().getParams());
                    try(Paramus p = Paramus.set(locateParams)){
                        p.setValue(LocateWrapper.PKVAL, cur.getSelectSqlDef().getLocation());
                        p.setValue(LocateWrapper.STARTFROM, cur.getSelectSqlDef().getOffset());
                    }
                    try (SQLCursor c = context.CreateCursor()
                            .init(conn, cur.getSelectSqlDef().getLocateSql(), locateParams).open();) {
                        if (c.reader().next()) {
                            int locatedPos = c.reader().getValue(1, int.class);
                            int offset = calcOffset(locatedPos, cur.getSelectSqlDef().getPageSize());
                            LOG.debug("Cursor \"{}\" successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cur.getBioCode(), cur.getSelectSqlDef().getLocation(), locatedPos, offset);
                            cur.getSelectSqlDef().setOffset(offset);
                            cur.getSelectSqlDef().setParamValue(PaginationWrapper.OFFSET, cur.getSelectSqlDef().getOffset())
                                    .setParamValue(PaginationWrapper.LAST, cur.getSelectSqlDef().getOffset() + cur.getSelectSqlDef().getPageSize());
                        } else {
                            LOG.debug("Cursor \"{}\" failed location to [{}] record by pk!!!", cur.getBioCode(), cur.getSelectSqlDef().getLocation());
                            result.exception(new BioError.LacationFail(cur.getSelectSqlDef().getLocation()));
                        }
                    }
                }

                StoreData data = new StoreData();
                data.setStoreId(request.getStoreId());
                data.setOffset(cur.getSelectSqlDef().getOffset());
                data.setPageSize(cur.getSelectSqlDef().getPageSize());
                data.setResults(totalCount);

                readStoreData(data, context, conn, cur);

                result.packet(data);
                return result.exception(null);
            }
        }, cursor);
        return response;
    }

    private BioRespBuilder.Data processCursorAsSelectableSinglePage(final User usr, final BioRequestJStoreGetDataSet request, final SQLContext ctx, final BioCursor cursor) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as SinglePage!!!", cursor.getBioCode());
        BioRespBuilder.Data response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.Data>() {
            @Override
            public BioRespBuilder.Data exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                tryPrepareSessionContext(usr.getUid(), conn);
                final BioRespBuilder.Data result = BioRespBuilder.data();
                result.bioCode(cur.getBioCode());

                StoreData data = new StoreData();
                data.setStoreId(request.getStoreId());
                data.setOffset(cur.getSelectSqlDef().getOffset());
                data.setPageSize(cur.getSelectSqlDef().getPageSize());
                int totalCount = readStoreData(data, context, conn, cur);

                if(totalCount == 0) {
                    LOG.debug("For cursor \"{}\" max records fetched [{}]! Try calc count total records!!!", cur.getBioCode(), MAX_RECORDS_FETCH_LIMIT);
                    try (SQLCursor c = context.CreateCursor()
                            .init(conn, cur.getSelectSqlDef().getTotalsSql(), cur.getSelectSqlDef().getParams()).open();) {
                        if (c.reader().next())
                            totalCount = c.reader().getValue(1, int.class);
                    }
                    LOG.debug("Total records of cursor \"{}\" - {}!!!", cur.getBioCode(), totalCount);
                }
                data.setResults(totalCount);
                result.packet(data);
                return result.exception(null);
            }
        }, cursor);
        return response;

    }

    private BioRespBuilder.Data processCursorAsSelectableSingleRecord(final User usr, final BioRequestJStoreGetRecord request, final SQLContext ctx, final BioCursor cursor) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as SinglePage!!!", cursor.getBioCode());
        BioRespBuilder.Data response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.Data>() {
            @Override
            public BioRespBuilder.Data exec(SQLContext context, Connection conn, BioCursor cursorDef) throws Exception {
                tryPrepareSessionContext(usr.getUid(), conn);
                final BioRespBuilder.Data result = BioRespBuilder.data();
                result.bioCode(cursorDef.getBioCode());

                cursorDef.getSelectSqlDef().setParamValue(GetrowWrapper.PKVAL, request.getId());

                StoreData data = new StoreData();
                data.setStoreId(request.getStoreId());
                readStoreData(data, context, conn, cursorDef);

                result.packet(data);
                return result.exception(null);
            }
        }, cursor);
        return response;

    }

    private static final String PARAM_CURUSR_UID =    "SYS_CURUSR_UID";
    private static final String PARAM_CURUSR_ROLES =  "SYS_CURUSR_ROLES";
    private static final String PARAM_CURUSR_GRANTS = "SYS_CURUSR_GRANTS";

    private static void applyCurrentUserParams(final User usr, BioCursor.SQLDef ... sqlDefs) {
        for(BioCursor.SQLDef sqlDef : sqlDefs) {
            if(sqlDef != null)
                try (Paramus p = Paramus.set(sqlDef.getParams())) {
                    p.setValue(PARAM_CURUSR_UID, usr.getUid(), false);
                    p.setValue(PARAM_CURUSR_ROLES, usr.getRoles(), false);
                    p.setValue(PARAM_CURUSR_GRANTS, usr.getGrants(), false);
                }
        }
    }

    private static void initSelectSqlDef(final BioCursor.SelectSQLDef sqlDef, final BioRequestJStoreGetDataSet request) {
        sqlDef.setParams(request.getBioParams());
        sqlDef.setOffset(request.getOffset());
        sqlDef.setPageSize(request.getPageSize());
        sqlDef.setLocation(request.getLocation());
        sqlDef.setFilter(request.getFilter());
        sqlDef.setSort(request.getSort());
    }

    private BioModule getActualModule(final BioRequest request) throws Exception {
        String altModuleKey = Utl.extractModuleKey(request.getBioCode());
        String defaultModuleKey = request.getModuleKey();
        String moduleKey = (Strings.isNullOrEmpty(altModuleKey) ? defaultModuleKey : altModuleKey);
        return moduleProvider.getModule(moduleKey);
    }

    private SQLContext getActualContext(final BioRequest request, final BioModule module) throws Exception {
        SQLContext ctx = sqlContextProvider.selectContext(module);
        if (ctx == null) {
            String defaultModuleKey = request.getModuleKey();
            BioModule ctxModule = moduleProvider.getModule(defaultModuleKey);
            ctx = sqlContextProvider.selectContext(ctxModule);
        }
        return ctx;
    }


    @Override
    public BioRespBuilder.Data getDataSet(final BioRequestJStoreGetDataSet request) throws Exception {
        LOG.debug("Process getDataSet for \"{}\" request...", request.getBioCode());
        try {
            final BioModule module = getActualModule(request);
            final SQLContext ctx = getActualContext(request, module);

            BioCursor cursor = module.getCursor(request.getBioCode());
            initSelectSqlDef(cursor.getSelectSqlDef(), request);

            final User usr = request.getUser();
            applyCurrentUserParams(usr, cursor.getSelectSqlDef());

            ctx.getWrappers().getWrapper(WrapQueryType.FILTERING).wrap(cursor.getSelectSqlDef());
            ctx.getWrappers().getWrapper(WrapQueryType.TOTALS).wrap(cursor.getSelectSqlDef());
            ctx.getWrappers().getWrapper(WrapQueryType.SORTING).wrap(cursor.getSelectSqlDef());
            ctx.getWrappers().getWrapper(WrapQueryType.LOCATE).wrap(cursor.getSelectSqlDef());
            if(cursor.getSelectSqlDef().getPageSize() < 0) {
                return processCursorAsSelectableSinglePage(usr, request, ctx, cursor);
            }else {
                ctx.getWrappers().getWrapper(WrapQueryType.PAGING).wrap(cursor.getSelectSqlDef());
                return processCursorAsSelectableWithPagging(usr, request, ctx, cursor);
            }
        } finally {
            LOG.debug("Processed getDataSet for \"{}\" - returning response...", request.getBioCode());
        }
    }

    @Override
    public BioRespBuilder.Data getRecord(final BioRequestJStoreGetRecord request) throws Exception {
        LOG.debug("Process getRecord for \"{}\" request...", request.getBioCode());
        try {
            final BioModule module = getActualModule(request);
            final SQLContext ctx = getActualContext(request, module);
            BioCursor cursor = module.getCursor(request.getBioCode());
            cursor.getSelectSqlDef().setParams(request.getBioParams());

            final User usr = request.getUser();
            applyCurrentUserParams(usr, cursor.getSelectSqlDef());

            ctx.getWrappers().getWrapper(WrapQueryType.GETROW).wrap(cursor.getSelectSqlDef());
            return processCursorAsSelectableSingleRecord(usr, request, ctx, cursor);
        } finally {
            LOG.debug("Processed getRecord for \"{}\" - returning response...", request.getBioCode());
        }
    }

    private static final String STD_PARAM_PREFIX = "p_";

    private static void processUpDelRow(final StoreRow row, final SQLContext ctx, final Connection conn, final BioCursor cursor) throws Exception {
        SQLStoredProc cmd = ctx.CreateStoredProc();
        RowChangeType changeType = row.getChangeType();
        BioCursor.SQLDef sqlDef = (Arrays.asList(RowChangeType.create, RowChangeType.update).contains(changeType) ? cursor.getUpdateSqlDef() : cursor.getDeleteSqlDef());
        if(sqlDef == null && Arrays.asList(RowChangeType.create, RowChangeType.update).contains(changeType))
            throw new Exception(String.format("For bio \"%s\" must be defined \"create/update\" sql!", cursor.getBioCode()));
        if(sqlDef == null && Arrays.asList(RowChangeType.delete).contains(changeType))
            throw new Exception(String.format("For bio \"%s\" must be defined \"delete\" sql!", cursor.getBioCode()));
        try(Paramus paramus = Paramus.set(sqlDef.getParams())) {
            for(Field field : cursor.getFields()) {
                paramus.add(Param.builder()
                        .name(STD_PARAM_PREFIX + field.getName().toLowerCase())
                        .value(row.getValue(field.getName()))
                        .type(field.getType())
                        .build(), true);
            }
            cmd.init(conn, sqlDef.getPreparedSql(), paramus.get());
        }
        cmd.execSQL();
        try(Paramus paramus = Paramus.set(cmd.getParams())) {
            for(Param p : paramus.get()) {
                if(Arrays.asList(Param.Direction.INOUT, Param.Direction.OUT).contains(p.getDirection())){
                    String fieldName = p.getName().substring(STD_PARAM_PREFIX.length());
                    Field fld = cursor.findField(fieldName);
                    row.setValue(fld.getName().toLowerCase(), p.getValue());
                }
            }
        }
    }

    private void applyParentRowToChildren(final BioCursor parentCursorDef, final StoreRow parentRow, final BioCursor cursorDef, final StoreRow row) {
        if(parentCursorDef != null && parentRow != null)
            for(Field field : cursorDef.getFields()) {
                if(row.getValue(field.getName()) == null) {
                    Field parentField = parentCursorDef.findField(field.getName());
                    if(parentField != null) {
                        Object parentValue = parentRow.getValue(parentField.getName());
                        if (parentValue != null)
                            row.setValue(field.getName().toLowerCase(), parentValue);
                    }
                }
            }
    }

    private BioRespBuilder.Data processRequestPost(final BioRequestJStorePost request, final SQLContext ctx, final Connection conn, final BioCursor parentCursorDef, final StoreRow parentRow, final User rootUsr) throws Exception {
        final User usr = (rootUsr != null) ? rootUsr : request.getUser();
        final BioModule module = getActualModule(request);
        final BioCursor cursorDef = module.getCursor(request.getBioCode());
        cursorDef.getSelectSqlDef().setParams(request.getBioParams());
        applyCurrentUserParams(usr, cursorDef.getUpdateSqlDef(), cursorDef.getDeleteSqlDef());

        final BioRespBuilder.Data result = BioRespBuilder.data();
        result.bioCode(request.getBioCode());

        StoreRow firstRow = null;
        for(StoreRow row : request.getModified()) {
            applyParentRowToChildren(parentCursorDef, parentRow, cursorDef, row);
            processUpDelRow(row, ctx, conn, cursorDef);
            if(firstRow == null)
                firstRow = row;
        }


        List<BioResponse> slaveResponses = new ArrayList<>();
        for(BioRequestJStorePost post : request.getSlavePostData()) {
            post.setModuleKey(request.getModuleKey()); // forward moduleKey
            BioResponse rsp = processRequestPost(post, ctx, conn, cursorDef, firstRow, usr).build();
            slaveResponses.add(rsp);
        }
        if(slaveResponses.size() > 0)
            result.slaveResponses(slaveResponses);

        StoreData data = new StoreData();
        data.setStoreId(request.getStoreId());
        data.setMetadata(new StoreMetadata());
        List<Field> cols = cursorDef.getFields();
        data.getMetadata().setFields(cols);
        data.setRows(request.getModified());

        result.packet(data);
        return result.exception(null);
    }

    @Override
    public BioRespBuilder.Data postDataSet(final BioRequestJStorePost request) throws Exception {
        LOG.debug("Process postDataSet for \"{}\" request...", request.getBioCode());
        try {
            final User usr = request.getUser();
            final BioModule module = getActualModule(request);
            final SQLContext ctx = getActualContext(request, module);
            BioRespBuilder.Data response = ctx.execBatch(new SQLAction<Object, BioRespBuilder.Data>() {
                @Override
                public BioRespBuilder.Data exec(SQLContext context, Connection conn, Object obj) throws Exception {
                    tryPrepareSessionContext(usr.getUid(), conn);
                    return processRequestPost(request, context, conn, null, null, null);
                }
            }, null);
            return response;
        } finally {
            LOG.debug("Processed postDataSet for \"{}\" - returning response...", request);
        }
    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");
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
