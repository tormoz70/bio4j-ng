package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import ru.bio4j.ng.database.commons.CrudReaderApi;
import ru.bio4j.ng.model.transport.ABeanPage;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.service.api.BioCursor;
import ru.bio4j.ng.service.types.BioCursorDeclaration;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetRecord;
import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.service.types.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;

public class ProviderGetRecord extends ProviderAn<BioRequestJStoreGetRecord> {

    protected static BioRespBuilder.DataBuilder processCursorAsSelectableSingleRecord(final BioRequestJStoreGetRecord request, final SQLContext context, final BioCursor cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as SinglePage!!!", cursor.getBioCode());

        ABeanPage beanPage = CrudReaderApi.loadRecord(request.getBioParams(), context, cursor, request.getUser());

        final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
        result.bioCode(cursor.getBioCode());

        StoreData data = StoreDataFactory.storeData();
        data.setStoreId(request.getStoreId());

        fillData(beanPage, data, LOG);

        result.packet(data);
        result.bioParams(request.getBioParams());
        result.id(request.getId());
        return result.exception(null);

    }

    @Override
    public void process(final BioRequestJStoreGetRecord request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process getRecord for \"{}\" request...", request.getBioCode());
        try {
            final BioCursor cursor = module.getCursor(request.getBioCode());
            //if(cursor.getSelectSqlDef() == null)
            //    throw new Exception(String.format("For bio \"%s\" must be defined \"select\" sql!", cursor.getBioCode()));

            //Field pkField = cursor.getSelectSqlDef().findPk();
            //if(pkField == null)
            //    throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
            //cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), pkField.getName()));
            BioRespBuilder.DataBuilder responseBuilder = processCursorAsSelectableSingleRecord(request, context, cursor, LOG);
            response.getWriter().append(responseBuilder.json());
        } finally {
            LOG.debug("Processed getRecord for \"{}\" - returning response...", request.getBioCode());
        }
    }

}
