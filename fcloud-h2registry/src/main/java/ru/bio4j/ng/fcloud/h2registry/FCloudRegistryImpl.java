package ru.bio4j.ng.fcloud.h2registry;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.FileSpec;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.ServiceBase;

import java.io.File;
import java.nio.file.*;
import java.sql.Connection;
import java.util.Dictionary;

@Component(managedservice="fcloud.registry.config")
@Instantiate
@Provides(specifications = FCloudRegistry.class)
public class FCloudRegistryImpl extends ServiceBase<FCloudRegistryConfig> implements FCloudRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(FCloudRegistryImpl.class);

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
    private volatile FCloudDBApi fCloudDBApi = null;
    private synchronized void initDatabase() throws Exception {
        if(!databaseInited) {
            fCloudDBApi = FCloudDBApi.getInstance();
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
            fCloudDBApi.initDB(conn);
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
        LOG.debug("Started");
    }

    private void storeFileSpec2db(final FileSpec fileSpec) throws Exception {
        initDatabase();
        Connection conn = getDbConnection();
        fCloudDBApi.storeFileSpec(conn, fileSpec);
    }

    private void removeFileSpecFromDb(final String fileUUID) throws Exception {
        initDatabase();
        Connection conn = getDbConnection();
        fCloudDBApi.removeFileSpec(conn, fileUUID);
    }

    private FileSpec readFileSpecFromDb(final String fileUUID) throws Exception {
        initDatabase();
        Connection conn = getDbConnection();
        return fCloudDBApi.readFileSpec(conn, fileUUID);
    }

    @Override
    public void regFile(final FileSpec fileSpec) throws Exception {
        storeFileSpec2db(fileSpec);
    }

    @Override
    public FileSpec getFileSpec(final String fileUid) throws Exception {
        return null;
    }

    @Override
    public void removeFile(final String fileUUID) throws Exception {
        removeFileSpecFromDb(fileUUID);
    }

}
