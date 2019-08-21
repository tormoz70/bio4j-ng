package ru.bio4j.ng.fcloud.h2registry;

import ru.bio4j.ng.model.transport.AnConfig;
import ru.bio4j.ng.model.transport.Prop;
import ru.bio4j.ng.model.transport.SQLContextConfig;

public class FCloudRegistryConfig extends AnConfig {

    @Prop(name = "fcloud.registry.server.port")
    private String serverPort = null;
    @Prop(name = "fcloud.registry.database.path")
    private String databasePath = null;
    @Prop(name = "fcloud.registry.database.username")
    private String username = null;
    @Prop(name = "fcloud.registry.database.password")
    private String password = null;


    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
