package ru.bio4j.ng.fcloud.h2registry;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Strings;
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


    private Connection getDbConnection() throws Exception {
        return H2Api.getInstance().getLocalConnection();
    }

    private static void initDbDirectory(final String databasePath) throws Exception {
        Path dbPath = new File(databasePath).toPath().toAbsolutePath();
        Files.createDirectories(dbPath.getParent());
    }

    private volatile boolean databaseInited = false;
    private volatile FCloudDBApi fCloudDBApi = null;
    private synchronized void initDatabase() throws Exception {
        if(!databaseInited) {
            String serverPort = this.getConfig().getServerPort();
            String databasePath = this.getConfig().getDatabasePath();
            String usrName = this.getConfig().getUsername();
            String passwd = this.getConfig().getPassword();
            int poolSize = this.getConfig().getPoolSize();
            if(LOG.isDebugEnabled())LOG.debug(String.format("Init FCLOUDREG database path(%s)...", databasePath));
            initDbDirectory(databasePath);
            if(LOG.isDebugEnabled())LOG.debug(String.format("FCLOUDREG database path(%s) inited!", databasePath));
            if(LOG.isDebugEnabled())LOG.debug(String.format("Starting FCLOUDREG database server(port: %s, path: %s, login: %s/%s, poolSize: %d)...", serverPort, databasePath, usrName, passwd, poolSize));
            H2Api.getInstance().startServer(serverPort, databasePath, usrName, passwd, poolSize);
            if(LOG.isDebugEnabled())LOG.debug(String.format("FCLOUDREG database server(port: %s) started!", H2Api.getInstance().getActualTcpPort()));

            fCloudDBApi = FCloudDBApi.getInstance();
            if(LOG.isDebugEnabled())LOG.debug("Connecting to FCLOUDREG database...");
            Connection conn = getDbConnection();
            if(LOG.isDebugEnabled())LOG.debug(String.format("FCLOUDREG database: Connection - %s", "opened", H2Api.getInstance().getActualUrl()));
            if(LOG.isDebugEnabled())LOG.debug("Init FCLOUDREG database...");
            fCloudDBApi.initDB(conn);
            if(LOG.isDebugEnabled())LOG.debug("FCLOUDREG database inited!");
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
        try(Connection conn = getDbConnection()) {
            fCloudDBApi.storeFileSpec(conn, fileSpec);
        }
    }

    private void removeFileSpecFromDb(final String fileUUID) throws Exception {
        initDatabase();
        try(Connection conn = getDbConnection()) {
            fCloudDBApi.removeFileSpec(conn, fileUUID);
        }
    }

    private FileSpec readFileSpecFromDb(final String fileUUID) throws Exception {
        initDatabase();
        try(Connection conn = getDbConnection()) {
            return fCloudDBApi.readFileSpec(conn, fileUUID);
        }
    }

    @Override
    public void regFile(final FileSpec fileSpec) throws Exception {
        storeFileSpec2db(fileSpec);
    }

    @Override
    public FileSpec getFileSpec(final String fileUid) throws Exception {
        return readFileSpecFromDb(fileUid);
    }

    @Override
    public void removeFile(final String fileUUID) throws Exception {
        removeFileSpecFromDb(fileUUID);
    }

}
