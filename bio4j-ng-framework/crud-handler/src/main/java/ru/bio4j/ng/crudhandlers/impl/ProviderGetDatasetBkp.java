package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import ru.bio4j.ng.commons.utils.Sqls;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.service.api.BioCursor;
import ru.bio4j.ng.service.types.BioCursorDeclaration;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLCursor;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.service.types.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by ayrat on 07.03.2016.
 */
public class ProviderGetDatasetBkp extends ProviderAn<BioRequestJStoreGetDataSet> {

    protected static int calcOffset(int locatedPos, int pageSize){
        int pg = ((int)((double)(locatedPos-1) / (double)pageSize) + 1);
        return (pg - 1) * pageSize;
    }

    private static BioRespBuilder.DataBuilder processCursorAsSelectableWithPagging(final BioRequestJStoreGetDataSet request, final SQLContext ctx, final BioCursor cursor, final Logger LOG) throws Exception {
        LOG.debug("Try open Cursor \"{}\" as MultiPage!!!", cursor.getBioCode());
        final BioRespBuilder.DataBuilder response = ctx.execBatch((context, conn, cur, usr) -> {
            final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder().exception(null);
            result.bioCode(cur.getBioCode());
            boolean requestCached = false; //requestCached(request, LOG);

            int totalCount = requestCached ? request.getTotalCount() : Sqls.UNKNOWN_RECS_TOTAL;
            if(request.getOffset() == (Sqls.UNKNOWN_RECS_TOTAL - request.getPageSize() + 1)) {
                LOG.debug("Try calc count records of cursor \"{}\"!!!", cur.getBioCode());
                try (SQLCursor c = context.createCursor()
                        .init(conn, cur.getSelectSqlDef().getTotalsSql(), cur.getSelectSqlDef().getParamDeclaration()).open(request.getBioParams(), null);) {
                    if (c.reader().next()) {
                        totalCount = c.reader().getValue(1, int.class);
                        int newOffset = (int)Math.floor(totalCount / request.getPageSize()) * request.getPageSize();
                        request.setOffset(newOffset);
                    }
                }
                LOG.debug("Count records of cursor \"{}\" - {}!!!", cur.getBioCode(), totalCount);
            }

            if(request.getLocation() != null) {
                LOG.debug("Try locate cursor \"{}\" to [{}] record by pk!!!", cur.getBioCode(), request.getLocation());
                try (SQLCursor c = context.createCursor()
                        .init(conn, cur.getSelectSqlDef().getLocateSql(), cur.getSelectSqlDef().getParamDeclaration()).open(request.getBioParams(), null);) {
                    if (c.reader().next()) {
                        int locatedPos = c.reader().getValue(1, int.class);
                        int offset = calcOffset(locatedPos, request.getPageSize());
                        LOG.debug("Cursor \"{}\" successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cur.getBioCode(), request.getLocation(), locatedPos, offset);
                        request.setOffset(offset);
                    } else {
                        LOG.debug("Cursor \"{}\" failed location to [{}] record by pk!!!", cur.getBioCode(), request.getLocation());
                        result.exception(new BioError.LocationFail(request.getLocation()));
                    }
                }
            }

            StoreData data = new StoreData();
            data.setStoreId(request.getStoreId());
            data.setOffset(request.getOffset());
            data.setPageSize(request.getPageSize());
            data.setPage((int)Math.floor(data.getOffset() / data.getPageSize()) + 1);
            data.setResults(totalCount);

            readStoreData(request, data, context, conn, cur, LOG);

            result.packet(data);

            if((data.getOffset() == 0) && (data.getRows().size() < data.getPageSize())){
                data.setResults(data.getRows().size());
            }

            return result.exception(null);
        }, cursor, request.getUser());
        response.bioParams(request.getBioParams());
        response.sort(request.getSort());
        response.filter(request.getFilter());
        response.location(request.getLocation());
        return response;
    }

    private static BioRespBuilder.DataBuilder processCursorAsSelectableSinglePage(final BioRequestJStoreGetDataSet request, final SQLContext ctx, final BioCursor cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as SinglePage!!!", cursor.getBioCode());
        BioRespBuilder.DataBuilder response = ctx.execBatch((context, conn, cur, usr) -> {
            final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
            result.bioCode(cur.getBioCode());

            StoreData data = new StoreData();
            data.setStoreId(request.getStoreId());
            data.setOffset(request.getOffset());
            data.setPageSize(request.getPageSize());
            int totalCount = readStoreData(request, data, context, conn, cur, LOG);

            if(totalCount == 0) {
                LOG.debug("For cursor \"{}\" max records fetched [{}]! Try calc count total records!!!", cur.getBioCode(), MAX_RECORDS_FETCH_LIMIT);
                try (SQLCursor c = context.createCursor()
                        .init(conn, cur.getSelectSqlDef().getTotalsSql(), cur.getSelectSqlDef().getParamDeclaration()).open(request.getBioParams(), null);) {
                    if (c.reader().next())
                        totalCount = c.reader().getValue(1, int.class);
                }
                LOG.debug("Total records of cursor \"{}\" - {}!!!", cur.getBioCode(), totalCount);
            }
            data.setResults(totalCount);
            result.packet(data);
            return result.exception(null);
        }, cursor, request.getUser());
        response.bioParams(request.getBioParams());
        response.sort(request.getSort());
        response.filter(request.getFilter());
        response.location(request.getLocation());
        return response;

    }

    @Override
    public void process(final BioRequestJStoreGetDataSet request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process getDataSet for \"{}\" request...", request.getBioCode());
        try {
            final BioCursor cursor = module.getCursor(request.getBioCode());

            cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), request.getFilter()));
            cursor.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));
            cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), request.getSort(), cursor.getSelectSqlDef().getFields()));
            if(request.getLocation() != null) {
                Field pkField = cursor.getSelectSqlDef().findPk();
                if(pkField == null)
                    throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
                cursor.getSelectSqlDef().setLocateSql(context.getWrappers().getLocateWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), pkField.getName()));
            }
            BioRespBuilder.DataBuilder responseBuilder;
            if(request.getPageSize() < 0) {
                responseBuilder = processCursorAsSelectableSinglePage(request, context, cursor, LOG);
            }else {
                if(request.getPageSize() > 0)
                    cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getPaginationWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));
                responseBuilder = processCursorAsSelectableWithPagging(request, context, cursor, LOG);
            }
            response.addHeader("X-Pagination-Current-Page", ""+request.getPage());
            response.addHeader("X-Pagination-Per-Page", ""+request.getPageSize());
//            response.addHeader("X-Pagination-Total-Count", ""+orgs.size());

            response.getWriter().append(responseBuilder.json());
        } finally {
            LOG.debug("Processed getDataSet for \"{}\" - returning response...", request.getBioCode());
        }
    }

}
