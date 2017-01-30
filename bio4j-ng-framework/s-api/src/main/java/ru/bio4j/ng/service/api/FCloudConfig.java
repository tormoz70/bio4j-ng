package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.commons.utils.Utl;

public class FCloudConfig {
    @Prop(name = "cloud.root.path")
    private String cloudRootPath = null;

    @Prop(name = "cloud.tmp.path")
    private String cloudTmpPath = null;

    public String getCloudRootPath() {
        return cloudRootPath;
    }

    public void setCloudRootPath(String cloudRootPath) {
        this.cloudRootPath = cloudRootPath;
    }

    public String getCloudTmpPath() {
        return cloudTmpPath;
    }

    public void setCloudTmpPath(String cloudTmpPath) {
        this.cloudTmpPath = cloudTmpPath;
    }
}
