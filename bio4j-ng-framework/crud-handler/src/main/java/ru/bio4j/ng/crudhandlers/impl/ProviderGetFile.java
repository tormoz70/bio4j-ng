package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.service.api.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class ProviderGetFile extends ProviderAn {

    private static final int BUFFER_SIZE = 4096;

    private static void processExec(final BioRequestGetFile request, final SQLContext sqlContext, final BioCursor cursor) throws Exception {
        final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
        final SQLStoredProc cmd = sqlContext.createStoredProc();
        final BioCursor.SQLDef sqlDef = cursor.getAfterselectSqlDef();
        if(sqlDef != null) {
            try(Paramus p = Paramus.set(sqlDef.getParams())){
                p.setValue("p_hash_code", request.getFileHashCode());
            }
            List<Param> r = sqlContext.execBatch((ctx, conn) -> {
                cmd.init(conn, sqlDef.getPreparedSql(), sqlDef.getParams());
                cmd.execSQL();
                return cmd.getParams();
            }, request.getUser());
            sqlDef.setParams(r);
        }
    }

    private static void processCursorAsFileProvider(final BioRequestGetFile request, final HttpServletResponse response, final SQLContext sqlContext, final BioCursor cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as JsonProvider!!!", cursor.getBioCode());

        final BioCursor.SQLDef sqlSelectDef = cursor.getSelectSqlDef();
        final BioCursor.SQLDef sqlExecDef = cursor.getExecSqlDef();

        sqlContext.execBatch((ctx, conn, cur) -> {
            tryPrepareSessionContext(request.getUser().getInnerUid(), conn);

            if(sqlSelectDef != null) {
                try(Paramus p = Paramus.set(sqlSelectDef.getParams())){
                    p.setValue("p_hash_code", request.getFileHashCode());
                }
                try (SQLCursor c = ctx.createCursor()
                        .init(conn, sqlSelectDef.getPreparedSql(), sqlSelectDef.getParams()).open();) {
                    if (c.reader().next()) {
                        ResultSet r = c.reader().getResultSet();
                        String fileName = r.getString("file_name");
                        Blob blob = r.getBlob("file_data");
                        int fileSize = r.getInt("file_size");
                        InputStream inputStream = blob.getBinaryStream();

                        String mimeType = "application/octet-stream";
                        // set content properties and header attributes for the response
                        response.setContentType(mimeType);
                        response.setContentLength(fileSize);
                        String headerKey = "Content-Disposition";
                        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
                        response.setHeader(headerKey, headerValue);

                        // writes the file to the client
                        OutputStream outStream = response.getOutputStream();

                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead = -1;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outStream.write(buffer, 0, bytesRead);
                        }

                        inputStream.close();
                        outStream.close();

                        processExec(request, ctx, cursor);
                    }
                }
            } else if(sqlExecDef != null) {
                final SQLStoredProc cmd = ctx.createStoredProc();
                List<Param> r = ctx.execBatch((context1, conn1) -> {
                    cmd.init(conn1, sqlExecDef.getPreparedSql(), sqlExecDef.getParams());
                    cmd.execSQL();
                    return cmd.getParams();
                }, request.getUser());
                try (Paramus p = Paramus.set(r)) {
                    String fileName = p.getValueByName(String.class, "file_name", true);
                    byte[] file = p.getValueByName(byte[].class, "file_data", true);

                    String mimeType = "application/octet-stream";
                    // set content properties and header attributes for the response
                    response.setContentType(mimeType);
                    response.setContentLength(file.length);
                    String headerKey = "Content-Disposition";
                    String headerValue = String.format("attachment; filename=\"%s\"", fileName);
                    response.setHeader(headerKey, headerValue);

                    OutputStream outStream = response.getOutputStream();
                    outStream.write(file, 0, file.length);
                    outStream.close();
                }

            }
            return null;
        }, cursor, request.getUser());

    }

    @Override
    public void process(final BioRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process getDataSet for \"{}\" request...", request.getBioCode());
        try {
            BioCursor cursor = module.getCursor(request);
            processCursorAsFileProvider((BioRequestGetFile) request, response, context, cursor, LOG);
        } finally {
            LOG.debug("Processed getDataSet for \"{}\" - returning response...", request.getBioCode());
        }
    }

}
