package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.FileSpec;

public interface FCloudRegistry {
    void regFile(final FileSpec fileSpec) throws Exception;
    FileSpec getFileSpec(final String fileUid) throws Exception;
    void removeFile(final String fileUUID) throws Exception;
}
