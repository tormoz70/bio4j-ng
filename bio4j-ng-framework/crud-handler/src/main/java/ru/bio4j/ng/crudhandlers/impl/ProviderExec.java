package ru.bio4j.ng.crudhandlers.impl;

import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.service.api.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.List;

public class ProviderExec extends ProviderAn {

    private static BioRespBuilder.DataBuilder processExec(final BioRequestStoredProg request, final SQLContext ctx, final BioCursor cursor) throws Exception {
        final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
        final SQLStoredProc cmd = ctx.createStoredProc();
        final BioCursor.SQLDef sqlDef = cursor.getExecSqlDef();
        if(sqlDef == null)
            throw new Exception(String.format("For bio \"%s\" must be defined \"execute\" sql!", cursor.getBioCode()));

        List<Param> r = ctx.execBatch(new SQLActionScalar<List<Param>>() {
            @Override
            public List<Param> exec(SQLContext context, Connection conn) throws Exception {
                cmd.init(conn, sqlDef.getPreparedSql(), sqlDef.getParams());
                cmd.execSQL();
                return cmd.getParams();
            }
        }, request.getUser());
        result.bioParams(r);
        return result.exception(null);
    }

    @Override
    public void process(final BioRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process exec for \"{}\" request...", request.getBioCode());
        try {
            final User usr = request.getUser();
            final BioCursor cursor = module.getCursor(request);

            BioRespBuilder.DataBuilder responseBuilder = processExec((BioRequestStoredProg)request, context, cursor);
            response.getWriter().append(responseBuilder.json());
        } finally {
            LOG.debug("Processed exec for \"{}\" - returning response...", request);
        }
    }

}
