package ru.bio4j.ng.crudhandlers.impl;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.types.BioQueryParams;
import ru.bio4j.ng.service.types.BioRespBuilder;
import ru.bio4j.ng.service.api.FCloudProvider;
import ru.bio4j.ng.service.api.FileSpec;
import ru.bio4j.ng.service.types.BioWrappedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ayrat on 07.03.2016.
 */
public class ProviderFCloud extends ProviderAn<BioRequestFCloud> {

    private FCloudProvider fcloudProvider;
    public ProviderFCloud(FCloudProvider fcloudProvider) {
        this.fcloudProvider = fcloudProvider;
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

    protected void getMetadata(final HttpServletResponse response) throws Exception {
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

    private void runImport(final BioRequestFCloud request, final HttpServletResponse response) throws Exception {
        fcloudProvider.getApi().runImport(request.getUser());
        BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().exception(null);
        response.getWriter().append(responseBuilder.json());
    }


    private void processUpload(final BioRequestFCloud request, final HttpServletResponse response) throws Exception {
        Collection<Part> parts = null;
        try {
            parts = request.getHttpRequest().getParts();
        } catch (Exception e) {}
        if(parts != null) {
            for (Part p : parts) {
                String fileName = Httpc.extractFileNameFromPart(p);
                if(Strings.isNullOrEmpty(fileName)) {
                    String paramName = p.getName();
                    String paramValue = Utl.readStream(p.getInputStream());
                    Paramus.setParamValue(request.getBioParams(), paramName, paramValue);
                    if (Strings.compare(paramName, BioQueryParams.CS_UPLOADEXTPARAM, true))
                        request.setExtParam(paramValue);

                }
            }

            List<FileSpec> files = new ArrayList<>();
            for (Part p : parts) {
                String fileName = Httpc.extractFileNameFromPart(p);
                if(!Strings.isNullOrEmpty(fileName)) {
                    FileSpec file = fcloudProvider.getApi().regFile(
                            request.getUploadUid(),
                            fileName,
                            p.getInputStream(),
                            p.getSize(),
                            null,
                            p.getContentType(),
                            request.getRemoteIP(),
                            request.getUploadType(),
                            request.getExtParam(),
                            request.getUploadDesc(),
                            request.getBioParams(),
                            request.getUser()
                    );
                    if (file != null) {
                        files.add(file);
                    }
                }
            }
            writeResult(files, response);
        } else
            writeResponse("[]", response);
    }

    private void processDownload(final BioRequestFCloud request, final HttpServletResponse response) throws Exception {
        FileSpec fileSpec = fcloudProvider.getApi().getFileSpec(request.getFileUid(), request.getUser());
        response.setContentType(fileSpec.getContentType());
        try(InputStream ios = fcloudProvider.getApi().getFile(request.getFileUid(), request.getUser())){
            Utl.writeInputToOutput(ios, response.getOutputStream());
        }
    }

    private void processRemove(final BioRequestFCloud request, final HttpServletResponse response) throws Exception {
        fcloudProvider.getApi().removeFile(request.getFileUid(), request.getUser());
        writeSuccess(response);
    }

    public void processFileSpec(final BioRequestFCloud request, final HttpServletResponse response) throws Exception {
        final User usr = request.getUser();
        final List<Param> bioParams = request.getBioParams();

//        final String fileNameFilter = Paramus.paramValue(bioParams, "qfcname", String.class, null);
//        final String fileDescFilter = Paramus.paramValue(bioParams, "qfcdesc", String.class, null);
//        final String fileParamFilter = Paramus.paramValue(bioParams, "qfcprm", String.class, null);
//        final String fileCTypeFilter = Paramus.paramValue(bioParams, "qfctype", String.class, null);
//        final String fileUpldUIDFilter = Paramus.paramValue(bioParams, "qfcupld", String.class, null);
//        final String fileHostFilter = Paramus.paramValue(bioParams, "qfchost", String.class, null);
//        final String fileUserFilter = Paramus.paramValue(bioParams, "qfcusr", String.class, null);
//        final String regFrom = Paramus.paramValue(bioParams, "qfcregfrm", String.class, null);
//        final String regTo = Paramus.paramValue(bioParams, "qfcregto", String.class, null);
//        final String fileFrom = Paramus.paramValue(bioParams, "qfcflfrom", String.class, null);
//        final String fileTo = Paramus.paramValue(bioParams, "qfcflto", String.class, null);
//        final String sizeFrom = Paramus.paramValue(bioParams, "qfcszfrom", String.class, null);
//        final String sizeTo = Paramus.paramValue(bioParams, "qfcszto", String.class, null);

        List<FileSpec> files = fcloudProvider.getApi().getFileList(bioParams, usr);
        writeResult(files, response);
    }

    @Override
    public void process(final BioRequestFCloud request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process postDataSet for \"{}\" request...", request.getBioCode());


        FCloudCommand fcmd = request.getCmd();
        switch(fcmd) {
            case UPLOAD:
                processUpload(request, response);
                break;
            case DOWNLOAD:
                processDownload(request, response);
                break;
            case REMOVE:
                processRemove(request, response);
                break;
            case FILESPEC:
                processFileSpec(request, response);
                break;
            case METADATA:
                getMetadata(response);
                break;
            case RUNIMPORT:
                runImport(request, response);
                break;
        }
    }

}
