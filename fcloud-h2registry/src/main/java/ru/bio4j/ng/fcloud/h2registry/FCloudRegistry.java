package ru.bio4j.ng.fcloud.h2registry;

import ru.bio4j.ng.model.transport.FileSpec;

public interface FCloudRegistry<T> {
    void regFile(final FileSpec fileSpec) throws Exception;
    FileSpec getFileSpec(final String fileUid) throws Exception;
    void removeFile(final String fileUUID) throws Exception;
}
