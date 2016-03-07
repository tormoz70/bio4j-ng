package ru.bio4j.ng.crudhandlers.impl;

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
import ru.bio4j.ng.service.api.BioModule;
import ru.bio4j.ng.service.api.BioRespBuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ayrat on 07.03.2016.
 */
public abstract class ProviderAn {
    protected final Logger LOG;

    protected static final int MAX_RECORDS_FETCH_LIMIT = 500;

    protected BioModule module;
    protected SQLContext context;

    public ProviderAn(){
        LOG = LoggerFactory.getLogger(this.getClass());
    }

    public void init(BioModule module, SQLContext context){
        this.module = module;
        this.context = context;
    }

    protected static void initSelectSqlDef(final BioCursor.SelectSQLDef sqlDef, final BioRequestJStoreGetDataSet request) {
        sqlDef.setParams(request.getBioParams());
        sqlDef.setOffset(request.getOffset());
        sqlDef.setPageSize(request.getPageSize());
        sqlDef.setLocation(request.getLocation());
        sqlDef.setFilter(request.getFilter());
        sqlDef.setSort(request.getSort());
    }

    protected static final String PARAM_CURUSR_UID =    "p_sys_curusr_uid";
    protected static final String PARAM_CURUSR_ROLES =  "p_sys_curusr_roles";
    protected static final String PARAM_CURUSR_GRANTS = "p_sys_curusr_grants";

    protected static void applyCurrentUserParams(final User usr, BioCursor.SQLDef ... sqlDefs) {
        for(BioCursor.SQLDef sqlDef : sqlDefs) {
            if(sqlDef != null)
                try (Paramus p = Paramus.set(sqlDef.getParams())) {
                    p.setValue(PARAM_CURUSR_UID, usr.getUid(), true);
                    p.setValue(PARAM_CURUSR_ROLES, usr.getRoles(), true);
                    p.setValue(PARAM_CURUSR_GRANTS, usr.getGrants(), true);
                }
        }
    }

    protected static void tryPrepareSessionContext(final String usrUID, final Connection conn) throws Exception {
//        LOG.debug("Try setting session context...");
//        try {
//            NamedParametersStatement cs = NamedParametersStatement.prepareStatement(conn, "{call biocontext.set_current_user(:usr_uid)}");
//            cs.setStringAtName("usr_uid", usrUID);
//            cs.execute();
//            LOG.debug("Session context - OK.");
//        } catch (Exception e) {
//            LOG.debug("Session context - ERROR. Message: {}", e.getMessage());
//        }
    }

    protected static String buildRequestKey(final BioRequestJStoreGetDataSet request) {
        String paramsUrl = null;
        List<Param> params = request.getBioParams();
        if (params != null) {
            try (Paramus p = Paramus.set(params)) {
                paramsUrl = p.buildUrlParams();
            }
        }
        return request.getBioCode()+(Strings.isNullOrEmpty(paramsUrl) ? "/<noparams>" : "/"+paramsUrl);
    }

    protected static TimedCache<Integer> requestCache = new TimedCache(300);
    protected static boolean requestCached(final BioRequestJStoreGetDataSet request, final Logger LOG) {
        String key = buildRequestKey(request);
        int check = Utl.nvl(requestCache.get(key), 0);
        requestCache.put(key, 1);
        LOG.debug("Processed requestCache, key: \"{}\" - check is [{}].", key, check);
        return check > 0;
    }

    protected static int readStoreData(final StoreData data, final SQLContext context, final Connection conn, final BioCursor cursorDef, final Logger LOG) throws Exception {
        LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        int totalCount = 0;
        try(SQLCursor c = context.createCursor()
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

    public abstract BioRespBuilder.Data process(final BioRequest request) throws Exception;

}
