package ru.givc.efond2.fcloud.module.impl;

import org.apache.felix.ipojo.annotations.*;
import org.h2.jdbcx.JdbcDataSource;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioModuleBase;
import ru.bio4j.ng.service.types.SQLContextConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.UUID;

@Component(managedservice="fcloud-registry.config")
@Instantiate
@Provides(specifications = BioFCloudApiModule.class,
        properties = {@StaticServiceProperty(
                name = "bioModuleKey",
                value = "fcloud-registry", // key must be always "fcloud-api" for security module
                type = "java.lang.String"
        )})
public class FCloudApiModuleImpl extends BioModuleBase<FCloudConfig> implements BioFCloudApiModule {
    private static final Logger LOG = LoggerFactory.getLogger(FCloudApiModuleImpl.class);

    @Requires
    private EventAdmin eventAdmin;
    @Requires
    private ModuleProvider moduleProvider;
    @Requires
    private SecurityProvider securityProvider;

    @Override
    public String getKey() {
        return "fcloud-registry";
    }

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

    @Override
    protected SQLContext createSQLContext(SQLContextConfig config) throws Exception {
        return null;
    }

    @Override
    public String getDescription() {
        return "FCloudRegistry module";
    }

    @Updated
    public synchronized void updated(Dictionary conf) throws Exception {
        doOnUpdated(conf, "fcloud-registry-config-updated");
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        fireEventModuleUpdated();
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

    private String storeUploaded2Tmp(InputStream inputStream, FileSpec file) throws Exception {
        String tmpPath = getCloudTmpPath().toString();
        tmpPath = Utl.normalizePath(tmpPath) + file.getFileUUID() + STORE_FILE_EXT;
        return Utl.storeInputStream(inputStream, tmpPath);
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
//        final BioAppModule module = this.moduleProvider.getAppModule("efond2");
//        final BioCursor cursor = module.getCursor("imports.register");

        Connection conn = DbH2Api.getConnection("jdbc:h2:˜/test", "sa", "sa");
        PreparedStatement stmnt =  conn.prepareStatement("select ? as rslt", ResultSet.TYPE_FORWARD_ONLY);
        ResultSet rs = stmnt.executeQuery();


//
//        final String fileUUID = fileSpec.getFileUUID();
//        final long recsCount = importMrc.calcRecords(fileUUID);
//
//        String rslt = context.execBatch((ctx, conn, cur, u) -> {
//            List<Param> prms = new ArrayList<>();
//            try (Paramus paramus = Paramus.set(prms)) {
//                paramus.add("p_filesuid", fileUUID)
//                        .add("p_filenameorig", fileSpec.getFileNameOrig())
//                        .add("p_md5", fileSpec.getMd5())
//                        .add("p_bsize", fileSpec.getFileSize())
//                        .add("p_rmtipaddr", fileSpec.getRemoteIpAddress())
//                        .add("p_mediatype", fileSpec.getContentType())
//                        .add("p_uploaduid", fileSpec.getUploadUID())
//                        .add("p_adesc", fileSpec.getAdesc())
//                        .add("p_extparam", fileSpec.getExtParam())
//                        .add("p_recscount", recsCount);
//            }
//            SQLStoredProc sp = ctx.createStoredProc();
//            sp.init(conn, cur.getExecSqlDef().getPreparedSql(), cur.getExecSqlDef().getParamDeclaration()).execSQL(prms, u);
//            return "OK";
//        }, cursor, usr);

    }

    private void removeFileSpecFromDb(final String fileUUID, User usr) throws Exception {
        final BioAppModule module = this.moduleProvider.getAppModule("efond2");
        final BioCursor cursor = module.getCursor("imports.remove");

        //        final SQLContext context = this.getSQLContext();
//
//        String rslt = context.execBatch((ctx, conn, cur, u) -> {
//
//            List<Param> prms = new ArrayList<>();
//            try (Paramus paramus = Paramus.set(prms)) {
//                paramus.add("p_filesuid", fileUUID);
//            }
//            SQLStoredProc sp = ctx.createStoredProc();
//            sp.init(conn, cur.getExecSqlDef().getPreparedSql(), cur.getExecSqlDef().getParamDeclaration()).execSQL(prms, u);
//            return "OK";
//        }, cursor, null);

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
            final long fileSize,
            final String contentType,
            final String remoteHost,
            final String uploadDesc,
            final String extParam,
            final User usr
    ) throws Exception {
        Path rootPath = getCloudRootPath();
        FileSpec file = null;
        if(!Strings.isNullOrEmpty(fileName)){
            file = new FileSpec();
            file.setUploadUID(uploadUID);
            file.setFileUUID(Utl.generateUUID());
            file.setFileNameOrig(fileName);
            file.setFileSize(fileSize);
            String _tmpFilePath = storeUploaded2Tmp(inputStream, file);
            String md5 = Utl.md5(_tmpFilePath);
            file.setMd5(md5);
            file.setContentType(contentType);
            file.setRemoteIpAddress(remoteHost);
            file.setAdesc(uploadDesc);
            file.setExtParam(extParam);
            String _tmpAttrsPath = storeAttrs(file, _tmpFilePath);
            String _storeFilePath = getStorePath(rootPath.toString(), file.getFileUUID());
            moveTmp2Store(Paths.get(_tmpFilePath), Paths.get(_storeFilePath));
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
    public InputStream getFile(final String fileUUID, final User usr) throws Exception {
        FCloudConfig cfg = this.getConfig();
        String rootPath = this.getCloudRootPath().toString();
        String _storeFilePath = getStorePath(rootPath, fileUUID);
        return Utl.openFile(_storeFilePath);
    }

    @Override
    public void removeFile(final String fileUUID, final User usr) throws Exception {
        FCloudConfig cfg = this.getConfig();
        String rootPath = this.getCloudRootPath().toString();
        String _storeFilePath = getStorePath(rootPath, fileUUID);
        Utl.deleteFile(_storeFilePath, true);
        removeFileSpecFromDb(fileUUID, usr);
    }

    @Override
    public void runImport(User usr) throws Exception {

    }


}
