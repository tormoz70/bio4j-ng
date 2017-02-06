package ru.bio4j.ng.fcloud.provider;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.BioCursor;
import ru.bio4j.ng.database.api.SQLAction;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLStoredProc;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.Dictionary;
import java.util.UUID;

import ru.bio4j.ng.model.transport.User;

@Component
@Instantiate
@Provides(specifications = FCloudProvider.class)
public class FCloudProviderImpl extends BioServiceBase implements FCloudProvider {
    private static final Logger LOG = LoggerFactory.getLogger(FCloudProviderImpl.class);

    @Requires
    private EventAdmin eventAdmin;

    @Requires
    private ModuleProvider moduleProvider;

    @Override
    protected EventAdmin getEventAdmin(){
        return eventAdmin;
    }

    private BioFCloudApiModule fcloudApi;

    private BioFCloudApiModule getApi() throws Exception {
        if(fcloudApi == null) {
            fcloudApi = moduleProvider.getFCloudApiModule("fcloud-api");
        }
        return fcloudApi;
    }

    @Override
    public void init(User usr) throws Exception {
        getApi().init(usr);
    }

    @Override
    public FileSpec regFile(
            final String uploadUID,
            final String fileName,
            final InputStream inputStream,
            final long fileSize,
            final String contentType,
            final String remoteHost,
            final String uploadDesc
    ) throws Exception {
        FileSpec file = getApi().regFile(uploadUID, fileName, inputStream, fileSize, contentType, remoteHost, uploadDesc);
        return file;
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        this.ready = true;
        LOG.debug("Started");
    }

    @Invalidate
    public void stop() throws Exception {
        LOG.debug("Stoping...");
        this.ready = false;
        LOG.debug("Stoped.");
    }

}
