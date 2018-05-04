package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
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

    protected static BioRespBuilder.DataBuilder processCursorAsSelectableSingleRecord(final BioRequestJStoreGetRecord request, final SQLContext ctx, final BioCursor cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as SinglePage!!!", cursor.getBioCode());
        BioRespBuilder.DataBuilder response = ctx.execBatch((context, conn, cursorDef, usr) -> {
            final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
            result.bioCode(cursorDef.getBioCode());

            StoreData data = new StoreData();
            data.setStoreId(request.getStoreId());
            readStoreData(request, data, context, conn, cursorDef, LOG);

            result.packet(data);
            return result.exception(null);
        }, cursor, request.getUser());
        response.bioParams(request.getBioParams());
        response.id(request.getId());
        return response;

    }

    @Override
    public void process(final BioRequestJStoreGetRecord request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process getRecord for \"{}\" request...", request.getBioCode());
        try {
            final BioCursor cursor = module.getCursor(request.getBioCode());
            if(cursor.getSelectSqlDef() == null)
                throw new Exception(String.format("For bio \"%s\" must be defined \"select\" sql!", cursor.getBioCode()));

            Field pkField = cursor.getSelectSqlDef().findPk();
            if(pkField == null)
                throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
            cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), pkField.getName()));
            BioRespBuilder.DataBuilder responseBuilder = processCursorAsSelectableSingleRecord(request, context, cursor, LOG);
            response.getWriter().append(responseBuilder.json());
        } finally {
            LOG.debug("Processed getRecord for \"{}\" - returning response...", request.getBioCode());
        }
    }

}
