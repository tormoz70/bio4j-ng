package ru.bio4j.service.ehcache.activator;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import ru.bio4j.service.ehcache.CacheService;
import ru.bio4j.service.ehcache.CacheServiceManagement;
import ru.bio4j.service.ehcache.impl.CacheServiceImpl;
import ru.bio4j.service.ehcache.impl.CacheServiceManagementImpl;

public class CacheServiceFactory implements ServiceFactory<CacheService> {

	private final Configuration configuration;

	public CacheServiceFactory(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	public CacheService getService(Bundle bundle, ServiceRegistration<CacheService> registration) {
		return new CacheServiceImpl(CacheManager.getCacheManager(configuration.getName()));
	}

	@Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<CacheService> registration,
			CacheService service) {
	}

	public CacheServiceManagement createServiceManagement() {
		return new CacheServiceManagementImpl(CacheManager.getCacheManager(configuration.getName()));
	}

}
