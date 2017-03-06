package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.BioRequestGetJson;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.service.api.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;

public class ProviderGetJson extends ProviderAn {

    private static BioRespBuilder.JsonBuilder processCursorAsJsonProvider(final BioRequestGetJson request, final SQLContext ctx, final BioCursor cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as JsonProvider!!!", cursor.getBioCode());
        BioRespBuilder.JsonBuilder response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.JsonBuilder>() {
            @Override
            public BioRespBuilder.JsonBuilder exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                tryPrepareSessionContext(request.getUser().getInnerUid(), conn);
                final BioRespBuilder.JsonBuilder result = BioRespBuilder.jsonBuilder();

                try(SQLCursor c = context.createCursor()
                        .init(conn, cur.getSelectSqlDef().getPreparedSql(), cur.getSelectSqlDef().getParams()).open();) {
                    while(c.reader().next()) {
                        List<Object> values = c.reader().getValues();
                        for(Object val : values)
                            result.getJsonBuilder().append(val);
                    }
                }
                return result.exception(null);
            }
        }, cursor, request.getUser());
        return response;

    }

    @Override
    public void process(final BioRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process getDataSet for \"{}\" request...", request.getBioCode());
        try {
            BioCursor cursor = module.getCursor(request);
            BioRespBuilder.JsonBuilder responseBuilder = processCursorAsJsonProvider((BioRequestGetJson) request, context, cursor, LOG);
            response.getWriter().append(responseBuilder.json());
        } finally {
            LOG.debug("Processed getDataSet for \"{}\" - returning response...", request.getBioCode());
        }
    }

}
