package ru.bio4j.ng.model.transport;


public class BioConfig extends AnConfig {

    @Prop(name = "bio.use.default.login.error.writer") // json | std
    private String useDefaultLoginErrorWriter = "json";
    @Prop(name = "bio.use.default.login.processing") // true | false
    private boolean useDefaultLoginProcessing = true;
    @Prop(name = "bio.service.odac")
    private String serviceNameOdac = "ru.bio4j.ng.service.api.AppService";
    @Prop(name = "bio.service.fcloud")
    private String serviceNameFCloud = "ru.bio4j.ng.service.api.FCloudApi";
    @Prop(name = "bio.service.security")
    private String serviceNameSecurity = "ru.bio4j.ng.service.api.SecurityService";
    @Prop(name = "bio.service.cache")
    private String serviceNameCache = "ru.bio4j.ng.service.api.CacheService";
    @Prop(name = "bio.service.fcloud.api")
    private String serviceFCloudApi = "fcloud-h2registry";



    @Prop(name = "ehcache.persistent.path")
    private String cachePersistentPath = "./ehcache-persistent";
    @Prop(name = "content.resolver.path")
    private String contentResolverPath = "./bio-content";
    @Prop(name = "tmp.path")
    private String tmpPath = "./bio-tmp";
    @Prop(name = "global.live-bio.content.path")
    private String liveBioContentPath = null;

//    public BioConfig() {
//        useDefaultLoginErrorWriter = "json";
//        useDefaultLoginProcessing = true;
//        serviceNameOdac = "ru.bio4j.ng.service.api.AppService";
//        serviceNameFCloud = "ru.bio4j.ng.service.api.FCloudApi";
//        serviceNameSecurity = "ru.bio4j.ng.service.api.SecurityService";
//        serviceNameCache = "ru.bio4j.ng.service.api.CacheService";
//        serviceFCloudApi = "fcloud-h2registry";
//        cachePersistentPath = "./ehcache-persistent";
//        contentResolverPath = "./bio-content";
//        tmpPath = "./bio-tmp";
//        liveBioContentPath = null;
//    }

    public String getLiveBioContentPath() {
        return liveBioContentPath;
    }

    public String getCachePersistentPath() {
        return cachePersistentPath;
    }

    public String getContentResolverPath() {
        return contentResolverPath;
    }

    public String getTmpPath() {
        return tmpPath;
    }

    public String getServiceNameOdac() {
        return serviceNameOdac;
    }

    public String getServiceNameFCloud() {
        return serviceNameFCloud;
    }

    public String getServiceNameSecurity() {
        return serviceNameSecurity;
    }

    public String getServiceNameCache() {
        return serviceNameCache;
    }

    public String getServiceFCloudApi() {
        return serviceFCloudApi;
    }

    public String getUseDefaultLoginErrorWriter() {
        return useDefaultLoginErrorWriter;
    }

    public Boolean getUseDefaultLoginProcessing() {
        return useDefaultLoginProcessing;
    }
}
