package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.util.List;

/**
 *  Created by ayrat on 08.05.14.
 */
public interface FCloudApi {
    void init(User usr) throws Exception;
    FileSpec regFile(
            final String uploadUID,
            final String fileName,
            final InputStream inputStream,
            final long fileSize,
            final String contentType,
            final String remoteHost,
            final String uploadDesc
    ) throws Exception;

    InputStream getFile(String fileUUID) throws Exception;
}
