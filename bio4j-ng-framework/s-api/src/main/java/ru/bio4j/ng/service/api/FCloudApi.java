package ru.bio4j.ng.service.api;

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
            final String extParam,
            final User usr
    ) throws Exception;

    List<FileSpec> getFileList(
            final String fileNameFilter,
            final String fileDescFilter,
            final String fileParamFilter,
            final String fileCTypeFilter,
            final String fileUpldUIDFilter,
            final String fileHostFilter,
            final String fileUserFilter,
            final String regFrom,
            final String regTo,
            final String fileFrom,
            final String fileTo,
            final String sizeFrom,
            final String sizeTo,
            final User usr
    ) throws Exception;

    InputStream getFile(final String fileUUID, final User usr) throws Exception;

    void removeFile(final String fileUUID, final User usr) throws Exception;

}
