package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Bundles;
import ru.bio4j.ng.service.api.HttpParamMap;
import ru.bio4j.ng.service.api.LoginProcessor;
import ru.bio4j.ng.service.api.SecurityErrorHandler;

public class RestHelper {
    private static final Logger LOG = LoggerFactory.getLogger(RestServiceBase.class);

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

    public static LoginProcessor getLPInstance() {
        return SingletonContainer.LOGINPROCESSOR;
    }
    public static SecurityErrorHandler getSEHInstance() {
        return SingletonContainer.SECURITYERRORHANDLER;
    }

    private static class SingletonContainer {
        public static final AppServiceTypeGetters SRVTYPES;
        public static final RestHelperMethods INSTANCE;
        public static final HttpParamMap HTTPPARAMMAP;
        public static final LoginProcessor LOGINPROCESSOR;
        public static final SecurityErrorHandler SECURITYERRORHANDLER;

        static {
            INSTANCE = Bundles.createServiceImplInWAR(RestHelperMethods.class, DefaultRestHelperMethods.class);
            SRVTYPES = Bundles.createServiceImplInWAR(AppServiceTypeGetters.class, DefaultAppServiceTypes.class);
            HTTPPARAMMAP = Bundles.createServiceImplInWAR(HttpParamMap.class, DefaultHttpParamMap.class);
            LOGINPROCESSOR = Bundles.createServiceImplInWAR(LoginProcessor.class, DefaultLoginProcessor.class);
            SECURITYERRORHANDLER = Bundles.createServiceImplInWAR(SecurityErrorHandler.class, DefaultSecurityErrorHandler.class);
        }
    }
}
