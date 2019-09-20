package ru.bio4j.ng.model.transport;


public class BioConfig extends AnConfig {

    @Prop(name = "bio.login.error.handler") // json | std
    private String useLoginErrorHandler = "json";
    @Prop(name = "bio.login.processing.handler") // true | false
    private Boolean useLoginProcessingHandler = true;
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

    public String getUseLoginErrorHandler() {
        return useLoginErrorHandler;
    }

    public Boolean getUseLoginProcessingHandler() {
        return useLoginProcessingHandler;
    }
}
