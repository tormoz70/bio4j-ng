package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.FileSpec;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface FCloudApi<T extends FileSpec> {

    /**
     * Register file in FCloud Service
     * @param fileSpec - file spec
     * @param inputStream - file
     * @param usr - user spec
     * @throws Exception
     */
    void regFile(final T fileSpec, final InputStream inputStream, final User usr) throws Exception;

    List<T> getFileList(
            final List<Param> params,
            final User usr
    ) throws Exception;

    T getFileSpec(
            final String fileUid,
            final User usr
    ) throws Exception;

    InputStream getFile(final String fileUUID, final User usr) throws Exception;

    void removeFile(final String fileUUID, final User usr) throws Exception;

}
