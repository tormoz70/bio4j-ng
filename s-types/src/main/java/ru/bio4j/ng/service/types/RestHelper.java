package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Bundles4WAR;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.service.api.*;

public class RestHelper {
    private static final Logger LOG = LoggerFactory.getLogger(RestHelper.class);

    private RestHelper() { /* hidden constructor */ }

    public static RestHelperMethods getInstance() {
        return SingletonContainer.INSTANCE;
    }

    public static AppServiceTypeGetters getAppTypes() {
        return SingletonContainer.SRVTYPES;
    }

    public static HttpParamMap getHttpParamMap() {
        return SingletonContainer.HTTPPARAMMAP;
    }

    public static SecurityApi securityApi() {
        return SingletonContainer.SECURITYAPI;
    }

    public static LoginProcessor loginProcessor() {
        return SingletonContainer.LOGINPROCESSOR;
    }

    public static ConfigProvider configProvider() {
        return SingletonContainer.CONFIG;
    }

    private static class SingletonContainer {
        public static final ConfigProvider CONFIG;
        public static final AppServiceTypeGetters SRVTYPES;
        public static final RestHelperMethods INSTANCE;
        public static final HttpParamMap HTTPPARAMMAP;
        public static final SecurityApi SECURITYAPI;
        public static final LoginProcessor LOGINPROCESSOR;

        static {
            CONFIG = Bundles4WAR.findBundleByInterface(ConfigProvider.class);
            if(CONFIG == null)
                throw Utl.wrapErrorAsRuntimeException(String.format("Service %s not found!", ConfigProvider.class));
            SRVTYPES = AppServiceTypesImpl.builder().build(CONFIG.getConfig());
            INSTANCE = Bundles4WAR.createLocalServiceImpl(RestHelperMethods.class, DefaultRestHelperMethods.class);
            if(INSTANCE == null)
                throw Utl.wrapErrorAsRuntimeException(String.format("Service %s not found!", RestHelperMethods.class));
            HTTPPARAMMAP = Bundles4WAR.createLocalServiceImpl(HttpParamMap.class, DefaultHttpParamMap.class);
            if(HTTPPARAMMAP == null)
                throw Utl.wrapErrorAsRuntimeException(String.format("Service %s not found!", HttpParamMap.class));
            SECURITYAPI = new SequrityApiImpl();
            SECURITYAPI.init(INSTANCE.getSecurityService());
            LOGINPROCESSOR = new DefaultLoginProcessorImpl();
            LOGINPROCESSOR.init(CONFIG.getConfig(), SECURITYAPI);
        }
    }
}
