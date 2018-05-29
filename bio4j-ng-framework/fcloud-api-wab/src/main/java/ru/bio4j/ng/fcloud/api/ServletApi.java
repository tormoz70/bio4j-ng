package ru.bio4j.ng.fcloud.api;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.*;
import sun.misc.IOUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServletApi extends BioServletApiBase {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private FCloudProvider fcloudProvider;

    protected void initFCloudProvider(ServletContext servletContext) {
        if(fcloudProvider == null) {
            try {
                fcloudProvider = Utl.getService(servletContext, FCloudProvider.class);
            } catch (IllegalStateException e) {
                fcloudProvider = null;
            }
        }
    }

    private static void writeResponse(String json, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.append(json);
    }

    private static void writeResult(List<FileSpec> files, HttpServletResponse response) throws IOException {
        String json = Jsons.encode(files);
        writeResponse(json, response);
    }

    private static void writeSuccess(HttpServletResponse response) throws IOException {
        ABean rslt = new ABean();
        rslt.put("success", true);
        String json = Jsons.encode(rslt);
        writeResponse(json, response);
    }


    private static void addFieldMeta(final List<Field> flds, final String fldName, final MetaType fldType, final String fldTitle){
        Field fld = new Field();
        fld.setId(flds.size());fld.setName(fldName);fld.setMetaType(fldType);fld.setTitle(fldTitle);flds.add(fld);
    }

    protected void getMetadata(final HttpServletRequest request, final HttpServletResponse response, final User usr) throws Exception {
        StoreMetadata meta = new StoreMetadata();
        List<Field> flds = new ArrayList<>();
        meta.setFields(flds);
        addFieldMeta(flds, "uploadUID", MetaType.STRING, "Уникальный идентификатор. Присваивается на стороне клиента, для однозначной идентификации после загрузки в хранилище");
        addFieldMeta(flds, "fileUUID", MetaType.STRING, "Уникальный идентификатор. Присваивается на стороне сервера после регистрации в базе данных хранилища");
        addFieldMeta(flds, "creDatetime", MetaType.DATE, "Дата/время регистрации файла в базе данных хранилища");
        addFieldMeta(flds, "fileNameOrig", MetaType.STRING, "Оригинальное имя файла, которое передано клиентом в хранилище");
        addFieldMeta(flds, "fileSize", MetaType.INTEGER, "Размер файла в байтах");
        addFieldMeta(flds, "fileDatetime", MetaType.DATE, "Дата/время файла, которое передано клиентом в хранилище (если не передано, тогда == creDatetime)");
        addFieldMeta(flds, "md5", MetaType.STRING, "Хэш-код MD5 для файла. Вычисляется при сохранении в хранилище");
        addFieldMeta(flds, "contentType", MetaType.STRING, "Сontent type - значение, которое передано клиентом в хранилище");
        addFieldMeta(flds, "remoteIpAddress", MetaType.STRING, "IP-адрес, с которого был загружен файл");
        addFieldMeta(flds, "adesc", MetaType.STRING, "Описание файла, которое передано клиентом в хранилище");
        addFieldMeta(flds, "threadUID", MetaType.STRING, "ID потока, который обработал файл");
        String json = Jsons.encode(flds);
        writeResponse(json, response);
    }

    public void processUpload(final HttpServletRequest request, final HttpServletResponse response, final User usr) throws Exception {
        BioWrappedRequest req = (BioWrappedRequest)request;
        Collection<Part> parts = null;
        try {
            parts = request.getParts();
        } catch (Exception e) {}
        if(parts != null) {
            //LOG.debug("Parts recived: {}", parts.size());
            List<FileSpec> files = new ArrayList<>();
            for (Part p : parts) {
                FileSpec file = fcloudProvider.getApi().regFile(
                        req.getBioQueryParams().fcloudUploadUid,
                        Httpc.extractFileNameFromPart(p),
                        p.getInputStream(),
                        p.getSize(),
                        null,
                        p.getContentType(),
                        request.getRemoteHost(),
                        req.getBioQueryParams().fcloudUploadDesc,
                        req.getBioQueryParams().fcloudExtParam,
                        usr
                );
                if(file != null)
                    files.add(file);
            }
            writeResult(files, response);
        } else
            writeResponse("[]", response);
        //BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().exception(null);
        //response.getWriter().append(responseBuilder.json());
    }

    public void processDownload(final HttpServletRequest request, final HttpServletResponse response, final User usr) throws Exception {
        BioWrappedRequest req = (BioWrappedRequest)request;
        InputStream fileInputStream = fcloudProvider.getApi().getFile(req.getBioQueryParams().fcloudFileUid, usr);
        Utl.writeInputToOutput(fileInputStream, response.getOutputStream());
    }

    public void processFileSpec(final HttpServletRequest request, final HttpServletResponse response, final User usr) throws Exception {
        BioWrappedRequest req = (BioWrappedRequest)request;
        List<Param> bioParams = ((BioWrappedRequest)request).getBioQueryParams().bioParams;

        final String fileNameFilter = Paramus.paramValue(bioParams, "qfcname", String.class, null);
        final String fileDescFilter = Paramus.paramValue(bioParams, "qfcdesc", String.class, null);
        final String fileParamFilter = Paramus.paramValue(bioParams, "qfcprm", String.class, null);
        final String fileCTypeFilter = Paramus.paramValue(bioParams, "qfctype", String.class, null);
        final String fileUpldUIDFilter = Paramus.paramValue(bioParams, "qfcupld", String.class, null);
        final String fileHostFilter = Paramus.paramValue(bioParams, "qfchost", String.class, null);
        final String fileUserFilter = Paramus.paramValue(bioParams, "qfcusr", String.class, null);
        final String regFrom = Paramus.paramValue(bioParams, "qfcregfrm", String.class, null);
        final String regTo = Paramus.paramValue(bioParams, "qfcregto", String.class, null);
        final String fileFrom = Paramus.paramValue(bioParams, "qfcflfrom", String.class, null);
        final String fileTo = Paramus.paramValue(bioParams, "qfcflto", String.class, null);
        final String sizeFrom = Paramus.paramValue(bioParams, "qfcszfrom", String.class, null);
        final String sizeTo = Paramus.paramValue(bioParams, "qfcszto", String.class, null);

        List<FileSpec> files = fcloudProvider.getApi().getFileList(fileNameFilter, fileDescFilter, fileParamFilter, fileCTypeFilter, fileUpldUIDFilter,
                fileHostFilter, fileUserFilter, regFrom, regTo, fileFrom, fileTo, sizeFrom, sizeTo, usr);
        writeResult(files, response);
    }

//    public void runImport(final HttpServletRequest request, final HttpServletResponse response, final User usr) throws Exception {
//        fcloudProvider.getApi().runImport(usr);
//        BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().exception(null);
//        response.getWriter().append(responseBuilder.json());
//    }

    public void processRemove(final HttpServletRequest request, final HttpServletResponse response, final User usr) throws Exception {
        BioWrappedRequest req = (BioWrappedRequest)request;
        fcloudProvider.getApi().removeFile(req.getBioQueryParams().fcloudFileUid, usr);
        writeSuccess(response);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");

        try {
            initServices(this.getServletContext());
            initFCloudProvider(this.getServletContext());

            BioQueryParams qprms = ((BioWrappedRequest) request).getBioQueryParams();
            User usr = ((BioWrappedRequest) request).getUser();
            FCloudCommand fcmd = qprms.fcloudCmd;
                switch(fcmd) {
                    case UPLOAD:
                        processUpload(request, response, usr);
                        break;
                    case DOWNLOAD:
                        processDownload(request, response, usr);
                        break;
                    case FILESPEC:
                        processFileSpec(request, response, usr);
                        break;
                    case REMOVE:
                        processRemove(request, response, usr);
                        break;
//                if(fcmd.compareToIgnoreCase("runimport") == 0)
//                    runImport(request, response, usr);
            }
        } catch (BioError e) {
            if(e.getErrCode() == 200)
                LOG.error("Server application error (Level-0)!", e);
            else
                LOG.error("Expected server error (Level-0)!", e);

            ErrorHandler.getInstance().writeError(e, response);
        } catch (Exception e) {
            LOG.error("Unexpected server error (Level-0)!", e);
            ErrorHandler.getInstance().writeError(BioError.wrap(e), response);
        }
    }

}
