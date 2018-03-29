package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.types.TimedCache;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.service.api.BioAppModule;
import ru.bio4j.ng.service.api.ConfigProvider;
import ru.bio4j.ng.service.api.ContentResolver;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ayrat on 07.03.2016.
 */
public abstract class ProviderAn<T extends BioRequest> {
    protected final Logger LOG;

    //TODO Перенести в настройки
    protected static final int MAX_RECORDS_FETCH_LIMIT = 2500;

    protected BioAppModule module;
    protected ContentResolver contentResolver;
    protected SQLContext context;
    protected ConfigProvider configProvider;

    public ProviderAn(){
        LOG = LoggerFactory.getLogger(this.getClass());
    }

    public void init(ConfigProvider configProvider, BioAppModule module, ContentResolver contentResolver, SQLContext context){
        this.module = module;
        this.contentResolver = contentResolver;
        this.context = context;
        this.configProvider = configProvider;
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

    protected static int readStoreData(final BioRequest request, final StoreData data, final SQLContext context, final Connection conn, final BioCursor cursorDef, final Logger LOG) throws Exception {
        LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        int totalCount = 0;
        long startTime = System.currentTimeMillis();
        try(SQLCursor c = context.createCursor()
                .init(conn, cursorDef.getSelectSqlDef()).open(request.getBioParams(), null);) {
            long estimatedTime = System.currentTimeMillis() - startTime;
            LOG.debug("Cursor \"{}\" opened in {} secs!!!", cursorDef.getBioCode(), Double.toString(estimatedTime/1000));
            data.setMetadata(new StoreMetadata());
            data.getMetadata().setReadonly(cursorDef.getReadOnly());
            data.getMetadata().setMultiSelection(cursorDef.getMultiSelection());
            List<Field> fields = cursorDef.getFields();
            data.getMetadata().setFields(fields);
            List<StoreRow> rows = new ArrayList<>();
            while(c.reader().next()) {
                StoreRow r = new StoreRow();
                Map<String, Object> vals = new HashMap<>();
                for (Field field : fields) {
                    DBField f = c.reader().getField(field.getName());
                    if (f != null) {
                        Object val = c.reader().getValue(f.getId());
                        Class<?> clazz = MetaTypeConverter.write(field.getMetaType());
                        Object valTyped = Converter.toType(val, clazz);
                        vals.put(field.getName().toLowerCase(), valTyped);
                    } else
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

    public abstract void process(final T request, final HttpServletResponse response) throws Exception;

}
