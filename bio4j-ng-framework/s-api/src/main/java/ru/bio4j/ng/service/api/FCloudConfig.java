package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.commons.utils.Utl;

public class FCloudConfig {
    @Prop(name = "files.cloud.path")
    private String filesCloudPath = null;

    @Prop(name = "thread.pool.size")
    private String threadPoolSize = null;

    public String getFilesCloudPath() {
        return filesCloudPath;
    }

    public void setFilesCloudPath(String filesCloudPath) {
        this.filesCloudPath = filesCloudPath;
    }

    public String getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(String threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
}
