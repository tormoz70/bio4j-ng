package ru.bio4j.service.processing.config;

import ru.bio4j.service.ServiceConfig;

import java.util.Dictionary;
import java.util.Hashtable;

import static ru.bio4j.util.Strings.empty;

public class ProcessingConfig extends ServiceConfig {

    public final static String CONNECT_URI = "processing.service.config.connectURI";
    public final static String USER = "processing.service.config.user";
    public final static String PASSWORD = "processing.service.config.password";
    public final static String MAX_POOL_SIZE = "processing.service.config.maxPoolSize";
    public final static String DRIVER_CLASSNAME = "processing.service.config.driver.name";

    private String connectURI;
    private String username;
    private String password;
    private String driverClassname;
    private int maxPoolSize;

    @Override
    public void config(Dictionary<String, ?> props) {
        if (props == null) {
            props = new Hashtable<String, Object>();
        }
        connectURI = getProperty(props, CONNECT_URI);
        username = getProperty(props, USER);
        password = getProperty(props, PASSWORD);
        driverClassname = getProperty(props, DRIVER_CLASSNAME);
        maxPoolSize = getIntProperty(props, MAX_POOL_SIZE);
    }

    public String getConnectURI() {
        return connectURI;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public String getDriverClassname() {
        return driverClassname;
    }

    public boolean isFilled() {
        return !empty(connectURI) && !empty(username) && !empty(password)
            && !empty(driverClassname);
    }

    @Override
    public String toString() {
        return "ProcessingConfig{" +
            "connectURI='" + connectURI + '\'' +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", driverClassname='" + driverClassname + '\'' +
            ", maxPoolSize=" + maxPoolSize +
            "} ";
    }
}
