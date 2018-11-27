package ru.bio4j.ng.service.api;



public class BioConfig extends AnConfig {
    @Prop(name = "bio.debug")
    private boolean bioDebug = false;

    @Prop(name = "bio.error.handler")
    private String errorHandler = "std";

    @Prop(name = "bio.fcloud.api")
    private String fcloudApi = "fcloud-h2registry";

    @Prop(name = "ehcache.persistent.path")
    private String cachePersistentPath = "./ehcache-persistent";

    @Prop(name = "content.resolver.path")
    private String contentResolverPath = "./bio-content";

    @Prop(name = "tmp.path")
    private String tmpPath = "./bio-tmp";

    @Prop(name = "global.live-bio.content.path")
    private String liveBioContentPath = null;


    public boolean isBioDebug() {
        return bioDebug;
    }

    public String getLiveBioContentPath() {
        return liveBioContentPath;
    }

    public void setLiveBioContentPath(String liveBioContentPath) {
        this.liveBioContentPath = liveBioContentPath;
    }

    public String getCachePersistentPath() {
        return cachePersistentPath;
    }

    public void setCachePersistentPath(String cachePersistentPath) {
        this.cachePersistentPath = cachePersistentPath;
    }

    public String getContentResolverPath() {
        return contentResolverPath;
    }

    public void setContentResolverPath(String contentResolverPath) {
        this.contentResolverPath = contentResolverPath;
    }

    public String getTmpPath() {
        return tmpPath;
    }

    public void setTmpPath(String tmpPath) {
        this.tmpPath = tmpPath;
    }

    public String getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(String errorHandler) {
        this.errorHandler = errorHandler;
    }

    public String getFcloudApi() {
        return fcloudApi;
    }

    public void setFcloudApi(String fcloudApi) {
        this.fcloudApi = fcloudApi;
    }
}
