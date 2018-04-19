package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.commons.wrappers.pagination.LocateWrapper;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreExpDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.service.api.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by ayrat on 07.03.2016.
 */
public class ProviderExpDataset extends ProviderAn<BioRequestJStoreExpDataSet> {

    protected static int calcOffset(int locatedPos, int pageSize){
        int pg = ((int)((double)(locatedPos-1) / (double)pageSize) + 1);
        return (pg - 1) * pageSize;
    }

    private static final int UNKNOWN_RECS_TOTAL = 999999999;

    private static BioRespBuilder.DataBuilder processCursorAsSelectableWithPagging(final BioRequestJStoreGetDataSet request, final SQLContext ctx, final BioCursorDeclaration cursor, final Logger LOG) throws Exception {
        LOG.debug("Try open Cursor \"{}\" as MultiPage!!!", cursor.getBioCode());
        final BioRespBuilder.DataBuilder response = ctx.execBatch((context, conn, cur, usr) -> {
//                tryPrepareSessionContext(request.getUser().getInnerUid(), conn);
            final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder().exception(null);
            result.bioCode(cur.getBioCode());
            boolean requestCached = false; //requestCached(request, LOG);

            int totalCount = requestCached ? request.getTotalCount() : UNKNOWN_RECS_TOTAL;
            if(request.getOffset() == (UNKNOWN_RECS_TOTAL - request.getPageSize() + 1)) {
//                if(totalCount == 0) {
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
            //TODO засунуть во входящие параметры
//            cur.getSelectSqlDef().setOffset(request.getOffset());

            if(request.getLocation() != null) {
                LOG.debug("Try locate cursor \"{}\" to [{}] record by pk!!!", cur.getBioCode(), request.getLocation());
                List<Param> locateParams = Paramus.clone(request.getBioParams());
                try(Paramus p = Paramus.set(locateParams)){
                    p.setValue(LocateWrapper.PKVAL, request.getLocation());
                    p.setValue(LocateWrapper.STARTFROM, request.getOffset());
                }
                try (SQLCursor c = context.createCursor()
                        .init(conn, cur.getSelectSqlDef().getLocateSql(), locateParams).open(null);) {
                    if (c.reader().next()) {
                        int locatedPos = c.reader().getValue(1, int.class);
                        int offset = calcOffset(locatedPos, request.getPageSize());
                        LOG.debug("Cursor \"{}\" successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cur.getBioCode(), request.getLocation(), locatedPos, offset);
                        //TODO это перенести во входящие параметры
//                        cur.getSelectSqlDef().setOffset(offset);
//                            cur.getSelectSqlDef().setParamValue(PaginationWrapper.OFFSET, cur.getSelectSqlDef().getOffset());
//                            cur.getSelectSqlDef().setParamValue(PaginationWrapper.LAST, cur.getSelectSqlDef().getOffset() + cur.getSelectSqlDef().getPageSize());
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
            data.setResults(totalCount);

            readStoreData(request, data, context, conn, cur, LOG);

            result.packet(data);

            if((data.getOffset() == 0) && (data.getRows().size() < data.getPageSize())){
                data.setResults(data.getRows().size());
            }

            return result.exception(null);
        }, cursor, request.getUser());
        return response;
    }

    private static BioRespBuilder.DataBuilder processCursorAsSelectableSinglePage(final BioRequestJStoreGetDataSet request, final SQLContext ctx, final BioCursorDeclaration cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as SinglePage!!!", cursor.getBioCode());
        BioRespBuilder.DataBuilder response = ctx.execBatch((context, conn, cur, usr) -> {
//                tryPrepareSessionContext(request.getUser().getInnerUid(), conn);
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
        return response;

    }

    private static void initSelectSqlDef(final BioCursorDeclaration.SelectSQLDef sqlDef, final BioRequestJStoreExpDataSet request) {
//        sqlDef.setParams(request.getBioParams());
        //sqlDef.setOffset(request.getOffset());
        //sqlDef.setPageSize(request.getPageSize());
        //sqlDef.setLocation(request.getLocation());
//        sqlDef.setFilter(request.getFilter());
//        sqlDef.setSort(request.getSort());
        //TODO Это все надо засунуть во входящие параметры
    }

    @Override
    public void process(final BioRequestJStoreExpDataSet request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process getDataSet for \"{}\" request...", request.getBioCode());
        try {
//            final BioCursorDeclaration cursor = contentResolver.getCursor(module.getKey(), request);
            final BioCursorDeclaration cursor = module.getCursor(request.getBioCode());
            initSelectSqlDef(cursor.getSelectSqlDef(), request);

            context.getWrappers().getWrapper(WrapQueryType.FILTERING).wrap(cursor.getSelectSqlDef(), request.getBioParams());
            //context.getWrappers().getWrapper(WrapQueryType.TOTALS).wrap(cursor.getSelectSqlDef());
            context.getWrappers().getWrapper(WrapQueryType.SORTING).wrap(cursor.getSelectSqlDef(), request.getBioParams());
            //context.getWrappers().getWrapper(WrapQueryType.LOCATE).wrap(cursor.getSelectSqlDef());
            BioRespBuilder.DataBuilder responseBuilder = processCursorAsSelectableSinglePage(request, context, cursor, LOG);
            response.getWriter().append(responseBuilder.json());
        } finally {
            LOG.debug("Processed getDataSet for \"{}\" - returning response...", request.getBioCode());
        }
    }

}
