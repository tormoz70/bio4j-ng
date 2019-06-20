package ru.bio4j.ng.service.types;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.service.api.AppService;

import java.util.Iterator;
import java.util.ServiceLoader;

public class RestHelper {
    private static final Logger LOG = LoggerFactory.getLogger(RestServiceBase.class);

    private RestHelper() { /* hidden constructor */ }

    public static RestHelperMethods getInstance() {
        return SingletonContainer.INSTANCE;
    }

    public static AppServiceTypes getAppTypes() {
        return SingletonContainer.SRVTYPES;
    }

    private static class SingletonContainer {
        public static final AppServiceTypes SRVTYPES;
        public static final RestHelperMethods INSTANCE;

        static {
            BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
            ClassLoader classLoader = bundleContext.getBundle().getClass().getClassLoader();

            RestHelperMethods restHelperMethods = null;
            Iterator<RestHelperMethods> loader1 =
                    ServiceLoader.load(RestHelperMethods.class, classLoader).iterator();
            if (loader1.hasNext())
                restHelperMethods = loader1.next();
            else
                restHelperMethods = new DefaultRestHelperMethods();
            LOG.debug(String.format("Found implementation for RestHelperMethods: %s", restHelperMethods.getClass().getName()));
            INSTANCE = restHelperMethods;

            AppServiceTypes appServiceTypes = null;
            Iterator<AppServiceTypes> loader2 =
                    ServiceLoader.load(AppServiceTypes.class, classLoader).iterator();
            if (loader2.hasNext())
                appServiceTypes = loader2.next();
            else
                appServiceTypes = new DefaultAppServiceTypes();
            LOG.debug(String.format("Found implementation for AppServiceTypes: %s", appServiceTypes.getClass().getName()));
            SRVTYPES = appServiceTypes;

            Class<?> appServiceTypesClazz = null;
            try {
                appServiceTypesClazz = bundleContext.getBundle().loadClass("ru.bio4j.ng.service.api.AppService");
            } catch (ClassNotFoundException e) {
                appServiceTypesClazz = null;
            }
            LOG.debug(String.format("Found appServiceTypesClazz: %s", appServiceTypesClazz));

        }
    }
}
