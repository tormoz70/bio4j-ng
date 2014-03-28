package ru.bio4j.service.ehcache.activator;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.DiskStoreConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.ServiceConstants;
import ru.bio4j.service.ServiceController;
import ru.bio4j.service.ehcache.CacheService;
import ru.bio4j.service.monitor.Monitor;
import ru.bio4j.util.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

public class CacheServiceController implements ServiceController {

	private final static String CACHE_CONFIG_FILE = "ehcache-config.xml";

	private final static String CACHE_PERSISTENT_PATH = "cache-persistent";
	
	private final static Logger LOG = LoggerFactory.getLogger(CacheServiceController.class);

	private final BundleContext bundleContext;

	private volatile Configuration cacheConfiguration;

	public CacheServiceController(BundleContext bundleContext) {
		super();
		this.bundleContext = bundleContext;
	}

	@Override
	public String getServiceName() {
		return "EhCache Service";
	}

	@Override
	public void start() throws Exception {
		createCacheConfiguration();
		CacheServiceFactory cacheServiceFactory = new CacheServiceFactory(cacheConfiguration);
		bundleContext.registerService(CacheService.class.getName(), cacheServiceFactory, createProperties());
		bundleContext.registerService(Monitor.class.getName(), cacheServiceFactory.createServiceManagement() , null);
		LOG.info("Cache Service Started");
	}

	private Dictionary<String, ?> createProperties() {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_RANKING, ServiceConstants.CACHE_SERVICE_RANK);
		return props;
	}
	
	@Override
	public void stop() throws Exception {
		if (cacheConfiguration != null) {
			CacheManager cacheManager = CacheManager.getCacheManager(cacheConfiguration.getName());
			if (cacheManager != null) {
				cacheManager.shutdown();
			}
			LOG.info("Cache Service Stopped");
		}
	}

	private void createCacheConfiguration() throws Exception {
		LOG.debug("Attempting to find cache configuration");
		InputStream configIn = getClass().getClassLoader().getResourceAsStream(CACHE_CONFIG_FILE);
		if (configIn == null) {
			LOG.debug("Could not find configuration file for cache service");
			throw new IllegalArgumentException("Could not find cache config file");
		}
		LOG.debug("Attempting to create new cache service");
		cacheConfiguration = ConfigurationFactory.parseConfiguration(configIn);
		cacheConfiguration.diskStore(createDiskStoreConfiguration());
		CacheManager.create(cacheConfiguration);
	}

	private DiskStoreConfiguration createDiskStoreConfiguration() {
		String systemPath = Utils.getSystemPath();
        String cachePath = (systemPath.contains(File.separator) ? systemPath : systemPath + File.separator) + CACHE_PERSISTENT_PATH;
        LOG.debug("Cache path is {}", cachePath);
		DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
		diskStoreConfiguration.setPath(cachePath);
		return diskStoreConfiguration;
	} 
	
}
