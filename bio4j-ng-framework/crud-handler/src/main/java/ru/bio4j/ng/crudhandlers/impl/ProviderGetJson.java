package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.service.types.BioCursorDeclaration;
import ru.bio4j.ng.model.transport.BioRequestGetJson;
import ru.bio4j.ng.service.types.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ProviderGetJson extends ProviderAn<BioRequestGetJson> {

    private static BioRespBuilder.JsonBuilder processCursorAsJsonProvider(final BioRequestGetJson request, final SQLContext ctx, final BioCursorDeclaration cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as JsonProvider!!!", cursor.getBioCode());
        BioRespBuilder.JsonBuilder response = ctx.execBatch((context, conn, cur, usr) -> {
//                tryPrepareSessionContext(request.getUser().getInnerUid(), conn);
            final BioRespBuilder.JsonBuilder result = BioRespBuilder.jsonBuilder();

            try(SQLCursor c = context.createCursor()
                    .init(conn, cur.getSelectSqlDef()).open(request.getBioParams(), null);) {
                while(c.reader().next()) {
                    List<Object> values = c.reader().getValues();
                    for(Object val : values)
                        result.getJsonBuilder().append(val);
                }
            }
            return result.exception(null);
        }, cursor, request.getUser());
        return response;

    }

    @Override
    public void process(final BioRequestGetJson request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process getDataSet for \"{}\" request...", request.getBioCode());
        try {
//            final BioCursorDeclaration cursor = contentResolver.getCursor(module.getKey(), request);
            final BioCursorDeclaration cursor = module.getCursor(request.getBioCode());
            BioRespBuilder.JsonBuilder responseBuilder = processCursorAsJsonProvider(request, context, cursor, LOG);
            response.getWriter().append(responseBuilder.json());
        } finally {
            LOG.debug("Processed getDataSet for \"{}\" - returning response...", request.getBioCode());
        }
    }

}
