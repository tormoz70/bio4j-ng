package ru.bio4j.ng.fcloud.h2registry;

import ru.bio4j.ng.model.transport.Prop;
import ru.bio4j.ng.model.transport.SQLContextConfig;

public class FCloudRegistryConfig extends SQLContextConfig {

    @Prop(name = "fcloud.registry.server.port")
    private String serverPort = null;

    @Prop(name = "fcloud.registry.connection.url")
    private String dbConnectionUrl = null;
    @Prop(name = "fcloud.registry.connection.username")
    private String dbConnectionUsername = null;
    @Prop(name = "fcloud.registry.connection.password")
    private String dbConnectionPassword = null;



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

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }
}
