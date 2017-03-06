package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.User;

import java.io.InputStream;

public interface FCloudApi {
    FileSpec regFile(
            final String uploadUID,
            final String fileName,
            final InputStream inputStream,
            final long fileSize,
            final String contentType,
            final String remoteHost,
            final String uploadDesc,
            final User usr
    ) throws Exception;

    InputStream getFile(final String fileUUID, final User usr) throws Exception;

    void runImport(final User usr) throws Exception;
}
