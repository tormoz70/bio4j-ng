package ru.bio4j.ng.fcloud.h2registry;

import ru.bio4j.ng.model.transport.Prop;
import ru.bio4j.ng.service.api.SQLContextConfig;

public class FCloudConfig extends SQLContextConfig {
    @Prop(name = "fcloud.root.path")
    private String cloudRootPath = null;

    @Prop(name = "fcloud.tmp.path")
    private String cloudTmpPath = null;

    @Prop(name = "fcloud.db.connection.url")
    private String dbConnectionUrl = null;
    @Prop(name = "fcloud.db.connection.username")
    private String dbConnectionUsername = null;
    @Prop(name = "fcloud.db.connection.password")
    private String dbConnectionPassword = null;


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

    @Override
    public String getDbConnectionUrl() {
        return dbConnectionUrl;
    }

    @Override
    public void setDbConnectionUrl(String dbConnectionUrl) {
        this.dbConnectionUrl = dbConnectionUrl;
    }

    public String getDbConnectionUsername() {
        return dbConnectionUsername;
    }

    public void setDbConnectionUsername(String dbConnectionUsername) {
        this.dbConnectionUsername = dbConnectionUsername;
    }

    public String getDbConnectionPassword() {
        return dbConnectionPassword;
    }

    public void setDbConnectionPassword(String dbConnectionPassword) {
        this.dbConnectionPassword = dbConnectionPassword;
    }
}
