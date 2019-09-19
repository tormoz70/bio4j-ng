package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Bundles4WAR;
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

    public static LoginProcessor getLogginProcessorInstance() {
        return SingletonContainer.LOGINPROCESSOR;
    }
    public static LoginErrorHandler getLoginErrorHandlerInstance() {
        return SingletonContainer.LOGINERRORHANDLER;
    }

    private static class SingletonContainer {
        public static final ConfigProvider CONFIG;
        public static final AppServiceTypeGetters SRVTYPES;
        public static final RestHelperMethods INSTANCE;
        public static final HttpParamMap HTTPPARAMMAP;
        public static final LoginProcessor LOGINPROCESSOR;
        public static final LoginErrorHandler LOGINERRORHANDLER;

        static {
            CONFIG = Bundles4WAR.findBundleByInterface(ConfigProvider.class);
            SRVTYPES = AppServiceTypesImpl.builder().build(CONFIG.getConfig());
            INSTANCE = Bundles4WAR.createLocalServiceImpl(RestHelperMethods.class, DefaultRestHelperMethods.class);
            HTTPPARAMMAP = Bundles4WAR.createLocalServiceImpl(HttpParamMap.class, DefaultHttpParamMap.class);
            LOGINPROCESSOR = Bundles4WAR.createServiceByName(CONFIG.getConfig().getLoginProcessingHandler());
            LOGINERRORHANDLER = Bundles4WAR.createServiceByName(CONFIG.getConfig().getLoginErrorHandler());
        }
    }
}
