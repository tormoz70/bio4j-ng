package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface FCloudApi {
    FileSpec regFile(
            final String uploadUID,
            final String fileName,
            final InputStream inputStream,
            final Date fileDatetime,
            final String contentType,
            final String remoteHost,
            final String uploadType,
            final String uploadExtParam,
            final String uploadDesc,
            final User usr
    ) throws Exception;

    List<FileSpec> getFileList(
            final List<Param> params,
            final User usr
    ) throws Exception;

    FileSpec getFileSpec(
            final String fileUid,
            final User usr
    ) throws Exception;

    InputStream getFile(final String fileUUID, final User usr) throws Exception;

    void removeFile(final String fileUUID, final User usr) throws Exception;

}
