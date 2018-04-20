package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
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

    protected static final int MAX_RECORDS_FETCH_LIMIT = 2500;

    protected static ABeanPage readStoreData(final List<Param> params, final SQLContext context, final Connection conn, final BioCursorDeclaration cursorDef, final Logger LOG) throws Exception {
        LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        ABeanPage result = new ABeanPage();
        result.setPaginationOffset(Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, int.class, 0));
        result.setRows(new ArrayList<>());

        long startTime = System.currentTimeMillis();
        List<Param> prms = params;
        try(SQLCursor c = context.createCursor()
                .init(conn, cursorDef.getSelectSqlDef().getSql(), cursorDef.getSelectSqlDef().getParamDeclaration()).open(prms, null);) {
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

    protected static int calcOffset(int locatedPos, int pageSize){
        int pg = ((int)((double)(locatedPos-1) / (double)pageSize) + 1);
        return (pg - 1) * pageSize;
    }

    private static ABeanPage loadPage(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final BioCursorDeclaration cursor,
            final User user,
            final Logger LOG) throws Exception {
        LOG.debug("Try load page of \"{}\" cursor!!!", cursor.getBioCode());
        final Object location = Paramus.paramValue(params, RestParamNames.LOCATE_PARAM_PKVAL, Object.class, 0);
        final int paginationOffset = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, int.class, 0);
        final int paginationPagesize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        final int paginationTotalcount = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, int.class, 0);

        context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef(), filter);
        context.getWrappers().getTotalsWrapper().wrap(cursor.getSelectSqlDef());
        context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef(), sort);
        context.getWrappers().getLocateWrapper().wrap(cursor.getSelectSqlDef(), location);
        context.getWrappers().getPaginationWrapper().wrap(cursor.getSelectSqlDef(), paginationPagesize);

        final ABeanPage result = context.execBatch((ctx, conn, cur, usr) -> {
            boolean requestCached = false; //requestCached(request, LOG);

            int factOffset = paginationOffset;
            int totalCount = requestCached ? paginationTotalcount : Sqls.UNKNOWN_RECS_TOTAL;
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
            ABeanPage beanPage = readStoreData(params, ctx, conn, cur, LOG);
            beanPage.setPaginationOffset(factOffset);
            return beanPage;
        }, cursor, user);
        return result;
    }

    private static ABeanPage loadAll(final List<Param> params, final SQLContext ctx, final BioCursorDeclaration cursor, User user, final Logger LOG) throws Exception {
        LOG.debug("Try load all records of \"{}\" cursor!!!", cursor.getBioCode());
        ABeanPage result = ctx.execBatch((context, conn, cur, usr) -> {
            return readStoreData(params, context, conn, cur, LOG);
        }, cursor, user);
        return result;

    }

    public ABeanPage process(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final BioCursorDeclaration cursor,
            User user,
            final Logger LOG) throws Exception {
        LOG.debug("Process for \"{}\" cursor...", cursor.getBioCode());
        final Object location = Paramus.paramValue(params, RestParamNames.LOCATE_PARAM_PKVAL, Object.class, 0);
        final int paginationPagesize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);

        context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef(), filter);
        context.getWrappers().getTotalsWrapper().wrap(cursor.getSelectSqlDef());
        context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef(), sort);
        context.getWrappers().getLocateWrapper().wrap(cursor.getSelectSqlDef(), location);
        ABeanPage beanPage;
        if(paginationPagesize > 0) {
            context.getWrappers().getPaginationWrapper().wrap(cursor.getSelectSqlDef(), paginationPagesize);
            beanPage = loadPage(params, context, cursor, user, LOG);
        }else {
            beanPage = loadAll(params, context, cursor, user, LOG);
        }
        LOG.debug("Processed getDataSet for \"{}\" - returning response...", cursor.getBioCode());
        return beanPage;
    }

}
