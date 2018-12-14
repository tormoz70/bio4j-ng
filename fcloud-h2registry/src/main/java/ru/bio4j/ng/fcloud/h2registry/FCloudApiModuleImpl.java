package ru.bio4j.ng.fcloud.h2registry;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioAppServiceBase;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.Connection;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

@Component(managedservice="fcloud-h2registry.config")
@Instantiate
@Provides(specifications = FCloudApi.class)
public class FCloudApiModuleImpl extends BioServiceBase<FCloudConfig> implements FCloudApi {
    private static final Logger LOG = LoggerFactory.getLogger(FCloudApiModuleImpl.class);

    @Requires
    private EventAdmin eventAdmin;

    @Override
    protected EventAdmin getEventAdmin() {
        return eventAdmin;
    }


    @Context
    private BundleContext bundleContext;

    @Override
    protected BundleContext bundleContext() {
        return bundleContext;
    }

    private String dbConnectionUrl;
    private String dbConnectionUsername;
    private String dbConnectionPassword;

    private Connection getDbConnection() throws Exception {
        return H2Api.getInstance().getConnection(dbConnectionUrl, dbConnectionUsername, dbConnectionPassword);
    }

    private static void initDbDirectory(final String dbConnUrl) throws Exception {
        String dbPathStr = dbConnUrl.substring("jdbc:h2:".length());
        Path dbPath = new File(dbPathStr).toPath().toAbsolutePath();
        Files.createDirectories(dbPath.getParent());
    }

    private volatile boolean databaseInited = false;
    private synchronized void initDatabase() throws Exception {
        if(!databaseInited) {
            LOG.debug("About to init FCLOUDREG database...");
            dbConnectionUrl = this.getConfig().getDbConnectionUrl();
            LOG.debug(String.format("About to init FCLOUDREG database: dbConnectionUrl = %s", dbConnectionUrl));
            initDbDirectory(dbConnectionUrl);
            LOG.debug(String.format("About to init FCLOUDREG database: initDbDirectory - %s", "done"));
            dbConnectionUsername = this.getConfig().getDbConnectionUsername();
            dbConnectionPassword = this.getConfig().getDbConnectionPassword();
            LOG.debug(String.format("About to init FCLOUDREG database: dbConnectionUsername = %s", dbConnectionUsername));
            LOG.debug(String.format("About to init FCLOUDREG database: dbConnectionPassword = %s", dbConnectionPassword));
            Connection conn = getDbConnection();
            LOG.debug(String.format("About to init FCLOUDREG database: Connection - %s", "opened"));
            FCloudDBApi.initDB(conn, "FCLOUDREG");
            LOG.debug("FCLOUDREG database inited!");
            databaseInited = true;
        }
    }

    @Updated
    public synchronized void updated(Dictionary conf) throws Exception {
        doOnUpdated(conf, "fcloud-h2registry-config-updated");
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
//        fireEventModuleUpdated();
        LOG.debug("Started");
    }

    private static final String STORE_ATTRS_EXT = ".attrs";
    private static final String STORE_FILE_EXT = ".data";

    private Path getCloudTmpPath() {
        FCloudConfig cfg = this.getConfig();
        return new File(cfg.getCloudTmpPath()).toPath().toAbsolutePath();
    }

    private Path getCloudRootPath() {
        FCloudConfig cfg = this.getConfig();
        return new File(cfg.getCloudRootPath()).toPath().toAbsolutePath();
    }

    private static class StoredFile{
        public String filePath;
        public int fileSize;
    }

    private StoredFile storeUploaded2Tmp(InputStream inputStream, String fileUid) throws Exception {
        StoredFile rslt = new StoredFile();
        rslt.filePath = getCloudTmpPath().toString();
        rslt.filePath = Utl.normalizePath(rslt.filePath) + fileUid + STORE_FILE_EXT;
        rslt.fileSize = Utl.storeInputStream(inputStream, rslt.filePath);
        return rslt;
    }



    private void moveTmp2Store(Path tmpPath, Path storePath) throws Exception {
        Files.createDirectories(storePath.getParent());
        Files.move(tmpPath, storePath, StandardCopyOption.ATOMIC_MOVE);
    }

    private void removeStore(String storePath) throws Exception {
        Path _storePath = Paths.get(storePath);
        Files.delete(_storePath);
    }

    private void storeFileSpec2db(final FileSpec fileSpec, User usr) throws Exception {
        Connection conn = getDbConnection();
        FCloudDBApi.storeFileSpec(conn, fileSpec, usr);
    }

    private void removeFileSpecFromDb(final String fileUUID, User usr) throws Exception {
        Connection conn = getDbConnection();
        FCloudDBApi.removeFileSpec(conn, fileUUID, usr);
    }

    private static String generateLocalPath(String uuid){
        if(uuid.length() == 32)
            return uuid.substring(0, 3) + File.separator + uuid.substring(3, 6) + File.separator + uuid.substring(6, 9);
        else
            return null;
    }

    private static String storeAttrs(FileSpec file, String tmpFileName) throws IOException {
        String attrTmpFileName = Utl.fileNameWithoutExt(tmpFileName) + STORE_ATTRS_EXT;
        String json = Jsons.encode(file);
        Utl.storeString(json, attrTmpFileName);
        return attrTmpFileName;
    }

    private String getStorePath(String storeRootPath, String fileUUID) {
        return Utl.normalizePath(Utl.normalizePath(storeRootPath) + generateLocalPath(fileUUID)) + fileUUID + STORE_FILE_EXT;
    }

    @Override
    public FileSpec regFile(
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
    ) throws Exception {
        initDatabase();
        Path rootPath = getCloudRootPath();
        FileSpec file = null;
        if(!Strings.isNullOrEmpty(fileName)){
            file = new FileSpec();
            file.setUploadUID(uploadUID);
            file.setFileUUID(Utl.generateUUID());
            file.setFileNameOrig(fileName);
            StoredFile _tmpFile = storeUploaded2Tmp(inputStream, file.getFileUUID());
            String md5 = Utl.md5(_tmpFile.filePath);
            file.setMd5(md5);
            file.setFileSize(_tmpFile.fileSize);
            file.setContentType(contentType);
            file.setRemoteIpAddress(remoteHost);
            file.setAdesc(uploadDesc);
            String _tmpAttrsPath = storeAttrs(file, _tmpFile.filePath);
            String _storeFilePath = getStorePath(rootPath.toString(), file.getFileUUID());
            moveTmp2Store(Paths.get(_tmpFile.filePath), Paths.get(_storeFilePath));
            String _storeAttrsPath = Utl.fileNameWithoutExt(_storeFilePath) + STORE_ATTRS_EXT;
            moveTmp2Store(Paths.get(_tmpAttrsPath), Paths.get(_storeAttrsPath));
            try {
                storeFileSpec2db(file, usr);
            } catch (Exception e) {
                removeStore(_storeFilePath);
                removeStore(_storeAttrsPath);
                throw  e;
            }
        }
        return file;
    }

    @Override
    public List<FileSpec> getFileList(
            final List<Param> params,
            final User usr
    ) throws Exception {
        initDatabase();
        return null;
    }

    @Override
    public FileSpec getFileSpec(
            final String fileUid,
            final User usr
    ) throws Exception {
        return null;
    }

    @Override
    public InputStream getFile(final String fileUUID, final User usr) throws Exception {
//        FCloudConfig cfg = this.getConfig();
        String rootPath = this.getCloudRootPath().toString();
        String _storeFilePath = getStorePath(rootPath, fileUUID);
        return Utl.openFile(_storeFilePath);
    }

    @Override
    public void removeFile(final String fileUUID, final User usr) throws Exception {
        initDatabase();
//        FCloudConfig cfg = this.getConfig();
        String rootPath = this.getCloudRootPath().toString();
        String _storeFilePath = getStorePath(rootPath, fileUUID);
        Utl.deleteFile(_storeFilePath, true);
        removeFileSpecFromDb(fileUUID, usr);
    }


}
