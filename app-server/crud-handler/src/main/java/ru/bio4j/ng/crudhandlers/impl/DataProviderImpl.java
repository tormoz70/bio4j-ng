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
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.model.transport.jstore.Field;
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

    private static int readStoreData(StoreData data, SQLContext context, Connection conn, BioCursor cursorDef) throws Exception {
        LOG.debug("Opening Cursor...");
        int totalCount = 0;
        try(SQLCursor c = context.CreateCursor()
                .init(conn, cursorDef.getSelectSqlDef().getPreparedSql(), cursorDef.getSelectSqlDef().getParams()).open();) {
            LOG.debug("Cursor opened!!!");
            data.setMetadata(new StoreMetadata());
            List<Field> cols = cursorDef.getFields();
            data.getMetadata().setFields(cols);
            List<StoreRow> rows = new ArrayList<>();
            while(c.reader().next()) {
                StoreRow r = new StoreRow();
                List<Object> vals = new ArrayList<>();
                for (Field col : cols) {
                    ru.bio4j.ng.database.api.Field f = c.reader().getField(col.getName());
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
            data.getMetadata().setFields(cols);
            data.setRows(rows);
        }
        return totalCount;
    }

    private BioRespBuilder.Data processCursorAsSelectableWithPagging(final User usr, final BioRequestJStoreGetDataSet request, final SQLContext ctx, final BioCursor cursor) throws Exception {
        LOG.debug("Try open Cursor as MultiPage!!!");
        final BioRespBuilder.Data response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.Data>() {
            @Override
            public BioRespBuilder.Data exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                tryPrepareSessionContext(usr.getUid(), conn);
                final BioRespBuilder.Data result = BioRespBuilder.data().exception(null);
                result.bioCode(cur.getBioCode());
                boolean requestCached = requestCached(request);

                int totalCount = requestCached ? request.getTotalCount() : 0;
                if(totalCount == 0) {
                    LOG.debug("Try calc count of cursor records!!!");
                    try (SQLCursor c = context.CreateCursor()
                            .init(conn, cur.getSelectSqlDef().getTotalsSql(), cur.getSelectSqlDef().getParams()).open();) {
                        if (c.reader().next())
                            totalCount = c.reader().getValue(1, int.class);
                    }
                    LOG.debug("Count of cursor records - {}!!!", totalCount);
                }

                if(cur.getSelectSqlDef().getLocation() != null) {
                    LOG.debug("Try locate cursor to [{}] record by pk!!!", cur.getSelectSqlDef().getLocation());
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
                            LOG.debug("Cursor successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cur.getSelectSqlDef().getLocation(), locatedPos, offset);
                            cur.getSelectSqlDef().setOffset(offset);
                            cur.getSelectSqlDef().setParamValue(PaginationWrapper.OFFSET, cur.getSelectSqlDef().getOffset())
                                    .setParamValue(PaginationWrapper.LAST, cur.getSelectSqlDef().getOffset() + cur.getSelectSqlDef().getPageSize());
                        } else {
                            LOG.debug("Cursor fail location to [{}] record by pk!!!", cur.getSelectSqlDef().getLocation());
                            result.exception(new BioError.LacationFail(cur.getSelectSqlDef().getLocation()));
                        }
                    }
                }

                StoreData data = new StoreData();
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

    private BioRespBuilder.Data processCursorAsSelectableSinglePage(final User usr, final SQLContext ctx, final BioCursor cursor) throws Exception {
        LOG.debug("Try open Cursor as SinglePage!!!");
        BioRespBuilder.Data response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.Data>() {
            @Override
            public BioRespBuilder.Data exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                tryPrepareSessionContext(usr.getUid(), conn);
                final BioRespBuilder.Data result = BioRespBuilder.data();
                result.bioCode(cur.getBioCode());

                //String preparedSQL = cur.getPreparedSql();

                StoreData data = new StoreData();
                data.setOffset(cur.getSelectSqlDef().getOffset());
                data.setPageSize(cur.getSelectSqlDef().getPageSize());
                int totalCount = readStoreData(data, context, conn, cur);

                if(totalCount == 0) {
                    LOG.debug("Max records fetched [{}]! Try calc count of total cursor records!!!", MAX_RECORDS_FETCH_LIMIT);
                    try (SQLCursor c = context.CreateCursor()
                            .init(conn, cur.getSelectSqlDef().getTotalsSql(), cur.getSelectSqlDef().getParams()).open();) {
                        if (c.reader().next())
                            totalCount = c.reader().getValue(1, int.class);
                    }
                    LOG.debug("Count of total cursor records - {}!!!", totalCount);
                }
                data.setResults(totalCount);
                result.packet(data);
                return result.exception(null);
            }
        }, cursor);
        return response;

    }

    private BioRespBuilder.Data processCursorAsSelectableSingleRecord(final User usr, final BioRequestJStoreGetRecord request, final SQLContext ctx, final BioCursor cursor) throws Exception {
        LOG.debug("Try open Cursor as SinglePage!!!");
        BioRespBuilder.Data response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.Data>() {
            @Override
            public BioRespBuilder.Data exec(SQLContext context, Connection conn, BioCursor cursorDef) throws Exception {
                tryPrepareSessionContext(usr.getUid(), conn);
                final BioRespBuilder.Data result = BioRespBuilder.data();
                result.bioCode(cursorDef.getBioCode());

                //String preparedSQL = cursorDef.getGetrowSql();
                cursorDef.getSelectSqlDef().setParamValue(GetrowWrapper.PKVAL, request.getId());

                StoreData data = new StoreData();
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

    private static void applyCurrentUserParams(User usr, BioCursor.SQLDef sqlDef) {
        try(Paramus p = Paramus.set(sqlDef.getParams())) {
            p.setValue(PARAM_CURUSR_UID, usr.getUid(), false);
            p.setValue(PARAM_CURUSR_ROLES, usr.getRoles(), false);
            p.setValue(PARAM_CURUSR_GRANTS, usr.getGrants(), false);
        }
    }

    private BioRespBuilder.Data processCursorAsExecutablePost(final User usr, final BioRequestJStorePost request, final SQLContext ctx, final BioCursor cursor) {
        return BioRespBuilder.data();
    }

    private static void initSelectSqlDef(final BioCursor.SelectSQLDef sqlDef, final BioRequestJStoreGetDataSet request) {
        sqlDef.setParams(request.getBioParams());
        sqlDef.setOffset(request.getOffset());
        sqlDef.setPageSize(request.getPageSize());
        sqlDef.setLocation(request.getLocation());
        sqlDef.setFilter(request.getFilter());
        sqlDef.setSort(request.getSort());
    }


    @Override
    public BioRespBuilder.Data getDataSet(final BioRequestJStoreGetDataSet request) throws Exception {
        LOG.debug("Process {} request...", request);
        try {
            String moduleKey = Utl.extractModuleKey(request.getBioCode());
            BioModule module = moduleProvider.getModule(moduleKey);
            BioCursor cursor = module.getCursor(request.getBioCode());
            initSelectSqlDef(cursor.getSelectSqlDef(), request);

            final User usr = request.getUser();
            applyCurrentUserParams(usr, cursor.getSelectSqlDef());

            SQLContext ctx = sqlContextProvider.selectContext(module);

            ctx.getWrappers().getWrapper(WrapQueryType.FILTERING).wrap(cursor.getSelectSqlDef());
            ctx.getWrappers().getWrapper(WrapQueryType.TOTALS).wrap(cursor.getSelectSqlDef());
            ctx.getWrappers().getWrapper(WrapQueryType.SORTING).wrap(cursor.getSelectSqlDef());
            ctx.getWrappers().getWrapper(WrapQueryType.LOCATE).wrap(cursor.getSelectSqlDef());
            if(cursor.getSelectSqlDef().getPageSize() < 0) {
                return processCursorAsSelectableSinglePage(usr, ctx, cursor);
            }else {
                ctx.getWrappers().getWrapper(WrapQueryType.PAGING).wrap(cursor.getSelectSqlDef());
                return processCursorAsSelectableWithPagging(usr, request, ctx, cursor);
            }
        } finally {
            LOG.debug("{} - returning response...", request);
        }
    }

    @Override
    public BioRespBuilder.Data getRecord(BioRequestJStoreGetRecord request) throws Exception {
        LOG.debug("Process {} request...", request);
        try {
            String moduleKey = Utl.extractModuleKey(request.getBioCode());
            BioModule module = moduleProvider.getModule(moduleKey);
            BioCursor cursor = module.getCursor(request.getBioCode());
            cursor.getSelectSqlDef().setParams(request.getBioParams());

            final User usr = request.getUser();
            applyCurrentUserParams(usr, cursor.getSelectSqlDef());

            SQLContext ctx = sqlContextProvider.selectContext(module);

            ctx.getWrappers().getWrapper(WrapQueryType.GETROW).wrap(cursor.getSelectSqlDef());
            return processCursorAsSelectableSingleRecord(usr, request, ctx, cursor);
        } finally {
            LOG.debug("{} - returning response...", request);
        }
    }

    @Override
    public BioRespBuilder.Data postDataSet(BioRequestJStorePost request) throws Exception {
        LOG.debug("Process {} request...", request);
        try {
            String moduleKey = Utl.extractModuleKey(request.getBioCode());
            BioModule module = moduleProvider.getModule(moduleKey);
            BioCursor cursor = module.getCursor(request.getBioCode());
            final User usr = request.getUser();
            //applyCurrentUserParams(usr, cursor);

            SQLContext ctx = sqlContextProvider.selectContext(module);

            return processCursorAsExecutablePost(usr, request, ctx, cursor);
        } finally {
            LOG.debug("{} - returning response...", request);
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
        //WrappersImpl.getInstance().init("oracle");
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
