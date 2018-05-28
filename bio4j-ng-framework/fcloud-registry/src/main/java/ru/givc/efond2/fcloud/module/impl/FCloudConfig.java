package ru.givc.efond2.fcloud.module.impl;

import ru.bio4j.ng.service.api.Prop;
import ru.bio4j.ng.service.types.SQLContextConfig;

public class FCloudConfig extends SQLContextConfig {
    @Prop(name = "fcloud.root.path")
    private String cloudRootPath = null;

    @Prop(name = "fcloud.tmp.path")
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
