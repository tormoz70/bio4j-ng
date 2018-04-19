package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.WrapQueryType;
import ru.bio4j.ng.database.commons.wrappers.filtering.GetrowWrapper;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetRecord;
import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.service.api.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ProviderGetRecord extends ProviderAn<BioRequestJStoreGetRecord> {

    protected static BioRespBuilder.DataBuilder processCursorAsSelectableSingleRecord(final BioRequestJStoreGetRecord request, final SQLContext ctx, final BioCursorDeclaration cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as SinglePage!!!", cursor.getBioCode());
        BioRespBuilder.DataBuilder response = ctx.execBatch((context, conn, cursorDef, usr) -> {
            final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
            result.bioCode(cursorDef.getBioCode());

//            cursorDef.getSelectSqlDef().setParamValue(GetrowWrapper.PKVAL, request.getId());
            List<Param> prms = request.getBioParams();
            Paramus.setParamValue(prms, GetrowWrapper.PKVAL, request.getId());

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
            final BioCursorDeclaration cursor = module.getCursor(request.getBioCode());
            if(cursor.getSelectSqlDef() == null)
                throw new Exception(String.format("For bio \"%s\" must be defined \"select\" sql!", cursor.getBioCode()));

            context.getWrappers().getWrapper(WrapQueryType.GETROW).wrap(cursor.getSelectSqlDef(), request.getBioParams());
            BioRespBuilder.DataBuilder responseBuilder = processCursorAsSelectableSingleRecord(request, context, cursor, LOG);
            response.getWriter().append(responseBuilder.json());
        } finally {
            LOG.debug("Processed getRecord for \"{}\" - returning response...", request.getBioCode());
        }
    }

}
