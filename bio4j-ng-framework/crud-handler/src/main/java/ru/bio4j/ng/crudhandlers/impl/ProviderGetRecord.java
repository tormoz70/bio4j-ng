package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import ru.bio4j.ng.database.api.BioCursor;
import ru.bio4j.ng.database.api.SQLAction;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.WrapQueryType;
import ru.bio4j.ng.database.commons.wrappers.filtering.GetrowWrapper;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetRecord;
import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.service.api.BioRespBuilder;

import java.sql.Connection;

/**
 * Created by ayrat on 07.03.2016.
 */
public class ProviderGetRecord extends ProviderAn {

    protected static BioRespBuilder.DataBuilder processCursorAsSelectableSingleRecord(final BioRequestJStoreGetRecord request, final SQLContext ctx, final BioCursor cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as SinglePage!!!", cursor.getBioCode());
        BioRespBuilder.DataBuilder response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.DataBuilder>() {
            @Override
            public BioRespBuilder.DataBuilder exec(SQLContext context, Connection conn, BioCursor cursorDef) throws Exception {
                tryPrepareSessionContext(request.getUser().getUid(), conn);
                final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
                result.bioCode(cursorDef.getBioCode());

                cursorDef.getSelectSqlDef().setParamValue(GetrowWrapper.PKVAL, request.getId());

                StoreData data = new StoreData();
                data.setStoreId(request.getStoreId());
                readStoreData(data, context, conn, cursorDef, LOG);

                result.packet(data);
                return result.exception(null);
            }
        }, cursor);
        return response;

    }

    public BioRespBuilder.Builder process(final BioRequest request) throws Exception {
        LOG.debug("Process getRecord for \"{}\" request...", request.getBioCode());
        try {
            BioCursor cursor = module.getCursor(request);
            cursor.getSelectSqlDef().setParams(request.getBioParams());

            context.getWrappers().getWrapper(WrapQueryType.GETROW).wrap(cursor.getSelectSqlDef());
            return processCursorAsSelectableSingleRecord((BioRequestJStoreGetRecord)request, context, cursor, LOG);
        } finally {
            LOG.debug("Processed getRecord for \"{}\" - returning response...", request.getBioCode());
        }
    }

}
