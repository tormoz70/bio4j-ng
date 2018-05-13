package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.service.api.BioCursor;
import ru.bio4j.ng.service.types.BioCursorDeclaration;
import ru.bio4j.ng.database.commons.CrudReaderApi;
import ru.bio4j.ng.model.transport.ABeanPage;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.service.types.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by ayrat on 07.03.2016.
 */
public class ProviderGetDataset extends ProviderAn<BioRequestJStoreGetDataSet> {

    protected static int calcOffset(int locatedPos, int pageSize){
        int pg = ((int)((double)(locatedPos-1) / (double)pageSize) + 1);
        return (pg - 1) * pageSize;
    }

    private static BioRespBuilder.DataBuilder processCursorAsSelectableWithPagging(final BioRequestJStoreGetDataSet request, final SQLContext ctx, final BioCursor cursor, final Logger LOG) throws Exception {
        LOG.debug("Try open Cursor \"{}\" as MultiPage!!!", cursor.getBioCode());

        ABeanPage beanPage = CrudReaderApi.loadPage(request.getBioParams(), request.getFilter(), request.getSort(), ctx, cursor, request.getUser());

        final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder().exception(null);
        result.bioCode(cursor.getBioCode());

        StoreData data = StoreDataFactory.storeData();
        data.setStoreId(request.getStoreId());
        data.setOffset(request.getOffset());
        data.setPageSize(request.getPageSize());
        data.setPage((int)Math.floor(data.getOffset() / data.getPageSize()) + 1);
        data.setStoreId(request.getStoreId());
        data.setPageSize(request.getPageSize());

        data.setOffset(beanPage.getPaginationOffset());
        data.setPage(beanPage.getPaginationPage());
        data.setResults(beanPage.getTotalCount());
        fillData(beanPage, data, LOG);

        result.packet(data);

        result.bioParams(request.getBioParams());
        result.sort(request.getSort());
        result.filter(request.getFilter());
        result.location(request.getLocation());
        return result.exception(null);
    }

    private static BioRespBuilder.DataBuilder processCursorAsSelectableSinglePage(final BioRequestJStoreGetDataSet request, final SQLContext ctx, final BioCursor cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as SinglePage!!!", cursor.getBioCode());

        ABeanPage beanPage = CrudReaderApi.loadAll(request.getBioParams(), request.getFilter(), request.getSort(), ctx, cursor, request.getUser());

        final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
        result.bioCode(cursor.getBioCode());

        StoreData data = StoreDataFactory.storeData();
        data.getMetadata().setReadonly(cursor.getReadOnly());
        data.getMetadata().setMultiSelection(cursor.getMultiSelection());
        data.setStoreId(request.getStoreId());
        data.setOffset(request.getOffset());
        data.setPageSize(request.getPageSize());
        data.setResults(beanPage.getPaginationCount());
        fillData(beanPage, data, LOG);
        result.packet(data);
        result.bioParams(request.getBioParams());
        result.sort(request.getSort());
        result.filter(request.getFilter());
        result.location(request.getLocation());
        return result.exception(null);

    }

    @Override
    public void process(final BioRequestJStoreGetDataSet request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process getDataSet for \"{}\" request...", request.getBioCode());
        try {
            final BioCursor cursor = module.getCursor(request.getBioCode());
            BioRespBuilder.DataBuilder responseBuilder;
            if(request.getPageSize() < 0) {
                responseBuilder = processCursorAsSelectableSinglePage(request, context, cursor, LOG);
            }else {
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
