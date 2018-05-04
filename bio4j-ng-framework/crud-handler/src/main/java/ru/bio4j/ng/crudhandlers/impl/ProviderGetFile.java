package ru.bio4j.ng.crudhandlers.impl;

import org.slf4j.Logger;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.service.api.BioCursor;
import ru.bio4j.ng.service.api.SQLDef;
import ru.bio4j.ng.service.api.SelectSQLDef;
import ru.bio4j.ng.service.api.UpdelexSQLDef;
import ru.bio4j.ng.service.types.BioCursorDeclaration;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.service.types.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.ResultSet;
import java.util.List;

public class ProviderGetFile extends ProviderAn<BioRequestGetFile> {

    private static final int BUFFER_SIZE = 4096;

    private static void processExec(final BioRequestGetFile request, final SQLContext sqlContext, final BioCursor cursor) throws Exception {
        final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
        final SQLStoredProc cmd = sqlContext.createStoredProc();
        final SQLDef sqlDef = cursor.getAfterselectSqlDef();
        if(sqlDef != null) {
            List<Param> r = sqlContext.execBatch((ctx, conn, usr) -> {
                cmd.init(conn, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration())
                        .execSQL(request.getBioParams(), usr);
                return cmd.getParams();
            }, request.getUser());
        }
    }

    private static class StoredBlobAttrs {
        public String fileName;
        public int fileSize;
        public String tmpFileName;
    }

    private static StoredBlobAttrs storeBlobToTmp(final String tmpPath, final BioRequestGetFile request, final SQLContext sqlContext, final BioCursor cursor, final Logger LOG) throws Exception {
        final StoredBlobAttrs rslt = new StoredBlobAttrs();
        final User user = request.getUser();
        final SelectSQLDef sqlSelectDef = cursor.getSelectSqlDef();
        final UpdelexSQLDef sqlExecDef = cursor.getExecSqlDef();
        sqlContext.execBatch((ctx, conn, cur, usr) -> {

            if(sqlSelectDef != null) {
                boolean fileStoredToTmpStorage = false;
                try (SQLCursor c = ctx.createCursor()
                        .init(conn, sqlSelectDef.getPreparedSql(), sqlSelectDef.getParamDeclaration()).open(request.getBioParams(), usr);) {
                    if (c.reader().next()) {
                        ResultSet r = c.reader().getResultSet();
                        rslt.fileName = r.getString("file_name");
                        rslt.fileSize = r.getInt("file_size");
                        Blob blob = r.getBlob("file_data");
                        try(InputStream inputStream = blob.getBinaryStream()) {
                            rslt.tmpFileName = Utl.generateTmpFileName(tmpPath, rslt.fileName);
                            Utl.storeInputStream(blob.getBinaryStream(), rslt.tmpFileName);
                        }
                        fileStoredToTmpStorage = true;
                    }
                }
                if(fileStoredToTmpStorage)
                    processExec(request, ctx, cursor);
            } else if(sqlExecDef != null) {
                final SQLStoredProc cmd = ctx.createStoredProc();
                List<Param> r = ctx.execBatch((context1, conn1, usr1) -> {
                    cmd.init(conn1, sqlExecDef.getPreparedSql(), sqlExecDef.getParamDeclaration());
                    cmd.execSQL(request.getBioParams(), usr1);
                    return cmd.getParams();
                }, request.getUser());
                try (Paramus p = Paramus.set(r)) {
                    rslt.fileName = p.getValueByName(String.class, "file_name", true);
                    byte[] blob = p.getValueByName(byte[].class, "file_data", true);
                    rslt.fileSize = blob.length;
                    rslt.tmpFileName = Utl.generateTmpFileName(tmpPath, rslt.fileName);
                    Utl.storeBlob(blob, rslt.tmpFileName);
                }

            }
            return null;
        }, cursor, request.getUser());
        return rslt;
    }

    private static void processCursorAsFileProvider(final String tmpPath, final BioRequestGetFile request, final HttpServletResponse response, final SQLContext sqlContext, final BioCursor cursor, final Logger LOG) throws Exception {
        LOG.debug("Try process Cursor \"{}\" as JsonProvider!!!", cursor.getBioCode());

        StoredBlobAttrs storedBlobAttrs = storeBlobToTmp(tmpPath, request, sqlContext, cursor, LOG);
        Path tmpFilePath = Paths.get(storedBlobAttrs.tmpFileName);
        if(Files.exists(tmpFilePath)) {
            String mimeType = "application/octet-stream";
            // set content properties and header attributes for the response
            response.setContentType(mimeType);
            response.setContentLength(storedBlobAttrs.fileSize);
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", storedBlobAttrs.fileName);
            response.setHeader(headerKey, headerValue);

            // writes the file to the client
            //response.getWriter().write(storedBlobAttrs.tmpFileName);

            File file = new File(storedBlobAttrs.tmpFileName);
            Utl.writeFileToOutput(file, response.getOutputStream());
            Files.delete(tmpFilePath);
        }
    }

    @Override
    public void process(final BioRequestGetFile request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process getDataSet for \"{}\" request...", request.getBioCode());
        String tmpPath = this.configProvider.getConfig().getTmpPath();
        try {
//            final BioCursorDeclaration cursor = contentResolver.getCursor(module.getKey(), request);
            final BioCursor cursor = module.getCursor(request.getBioCode());
            processCursorAsFileProvider(tmpPath, request, response, context, cursor, LOG);
        } finally {
            LOG.debug("Processed getDataSet for \"{}\" - returning response...", request.getBioCode());
        }
    }

}
