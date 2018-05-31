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
            final long fileSize,
            final Date fileDatetime,
            final String contentType,
            final String remoteHost,
            final String uploadDesc,
            final List<Param> params,
            final User usr
    ) throws Exception;

    List<FileSpec> getFileList(
            final List<Param> params,
            final User usr
    ) throws Exception;

    InputStream getFile(final String fileUUID, final User usr) throws Exception;

    void removeFile(final String fileUUID, final User usr) throws Exception;

}
