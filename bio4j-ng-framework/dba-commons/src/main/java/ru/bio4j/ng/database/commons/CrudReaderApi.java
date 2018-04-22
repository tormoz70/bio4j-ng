package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Sqls;
import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.api.DBField;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLCursor;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.RestParamNames;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class CrudReaderApi {
    protected final static Logger LOG = LoggerFactory.getLogger(CrudReaderApi.class);

    private static final int MAX_RECORDS_FETCH_LIMIT = 2500;

    private static ABeanPage readStoreData(final List<Param> params, final SQLContext context, final Connection conn, final BioCursorDeclaration cursorDef) throws Exception {
        LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        ABeanPage result = new ABeanPage();
        result.setTotalCount(Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, int.class, 0));
        result.setPaginationOffset(Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, int.class, 0));
        result.setRows(new ArrayList<>());

        long startTime = System.currentTimeMillis();
        List<Param> prms = params;
        try(SQLCursor c = context.createCursor()
                .init(conn, cursorDef.getSelectSqlDef()).open(prms, null);) {
            long estimatedTime = System.currentTimeMillis() - startTime;
            LOG.debug("Cursor \"{}\" opened in {} secs!!!", cursorDef.getBioCode(), Double.toString(estimatedTime/1000));
            result.setMetadata(cursorDef.getFields());
            while(c.reader().next()) {
                ABean bean = new ABean();
                for (Field field : result.getMetadata()) {
                    DBField f = c.reader().getField(field.getName());
                    if (f != null) {
                        Object val = c.reader().getValue(f.getId());
                        Class<?> clazz = MetaTypeConverter.write(field.getMetaType());
                        Object valTyped = Converter.toType(val, clazz);
                        bean.put(field.getName().toLowerCase(), valTyped);
                    } else
                        bean.put(field.getName().toLowerCase(), null);
                }
                result.getRows().add(bean);
                if(result.getRows().size() >= MAX_RECORDS_FETCH_LIMIT) {
                    break;
                }
            }
            LOG.debug("Cursor \"{}\" fetched! {} - records loaded.", cursorDef.getBioCode(), result.getRows().size());
        }
        result.setPaginationCount(result.getRows().size());
        int pageSize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        result.setPaginationPage(pageSize > 0 ? (int)Math.floor(result.getPaginationOffset() / pageSize) + 1 : 0);
        return result;
    }

    private static int calcOffset(int locatedPos, int pageSize){
        int pg = ((int)((double)(locatedPos-1) / (double)pageSize) + 1);
        return (pg - 1) * pageSize;
    }

    public static ABeanPage loadPage(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final BioCursorDeclaration cursor,
            final User user) throws Exception {
        final Object location = Paramus.paramValue(params, RestParamNames.LOCATE_PARAM_PKVAL, java.lang.Object.class, null);
        final int paginationOffset = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, int.class, 0);
        final int paginationPagesize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        final int paginationTotalcount = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, int.class, Sqls.UNKNOWN_RECS_TOTAL);

        context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef(), filter);
        context.getWrappers().getTotalsWrapper().wrap(cursor.getSelectSqlDef());
        context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef(), sort);
        if(location != null)
            context.getWrappers().getLocateWrapper().wrap(cursor.getSelectSqlDef());
        if(paginationPagesize > 0)
            context.getWrappers().getPaginationWrapper().wrap(cursor.getSelectSqlDef());

        final ABeanPage result = context.execBatch((ctx, conn, cur, usr) -> {
            int factOffset = paginationOffset;
            int totalCount = paginationTotalcount;
            if(paginationOffset == (Sqls.UNKNOWN_RECS_TOTAL - paginationPagesize + 1)) {
                LOG.debug("Try calc count of records of cursor \"{}\"!!!", cur.getBioCode());
                try (SQLCursor c = ctx.createCursor()
                        .init(conn, cur.getSelectSqlDef().getTotalsSql(), cur.getSelectSqlDef().getParamDeclaration()).open(params, null);) {
                    if (c.reader().next()) {
                        totalCount = c.reader().getValue(1, int.class);
                        factOffset = (int)Math.floor(totalCount / paginationOffset) * paginationPagesize;
                    }
                }
                LOG.debug("Count of records of cursor \"{}\" - {}!!!", cur.getBioCode(), totalCount);
            }

            if(location != null) {
                LOG.debug("Try locate cursor \"{}\" to [{}] record by pk!!!", cur.getBioCode(), location);
                try (SQLCursor c = ctx.createCursor()
                        .init(conn, cur.getSelectSqlDef().getLocateSql(), cur.getSelectSqlDef().getParamDeclaration()).open(params, usr);) {
                    if (c.reader().next()) {
                        int locatedPos = c.reader().getValue(1, int.class);
                        factOffset = calcOffset(locatedPos, paginationPagesize);
                        LOG.debug("Cursor \"{}\" successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cur.getBioCode(), location, locatedPos, factOffset);
                    } else {
                        LOG.debug("Cursor \"{}\" failed location to [{}] record by pk!!!", cur.getBioCode(), location);
                    }
                }
            }
            Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, factOffset);
            Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, totalCount);
            Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_LAST, factOffset+paginationPagesize);
            return readStoreData(params, ctx, conn, cur);
        }, cursor, user);
        return result;
    }

    public static ABeanPage loadAll(final List<Param> params, final Filter filter, final List<Sort> sort, final SQLContext context, final BioCursorDeclaration cursor, User user) throws Exception {
        context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef(), filter);
        context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef(), sort);
        ABeanPage result = context.execBatch((ctx, conn, cur, usr) -> {
            return readStoreData(params, ctx, conn, cur);
        }, cursor, user);
        return result;
    }

    public static ABeanPage loadRecord(final List<Param> params, final SQLContext context, final BioCursorDeclaration cursor, User user) throws Exception {
        context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef());
        ABeanPage result = context.execBatch((ctx, conn, cur, usr) -> {
            return readStoreData(params, ctx, conn, cur);
        }, cursor, user);
        return result;
    }

}
