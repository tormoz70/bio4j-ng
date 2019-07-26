package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.FileSpec;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.SpaceStat;
import ru.bio4j.ng.model.transport.User;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface FCloudApi {

    /**
     * Register file in FCloud Service
     * @param fileSpec - file spec
     * @param inputStream - file
     * @param usr - user spec
     * @throws Exception
     */
    void regFile(final FileSpec fileSpec, final InputStream inputStream, final User usr) throws Exception;

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

    HashMap<String, SpaceStat> getSpaceStat();

    String getServiceUID();

}
