package ru.bio4j.service.file.config;

import ru.bio4j.service.ServiceConfig;

import java.util.Dictionary;
import java.util.Hashtable;

import static ru.bio4j.util.Strings.empty;

public class FileContentResolverConfig extends ServiceConfig {

    public final static String PATH = "query.content.files.path";

    private String path;

    @Override
    public void config(Dictionary<String, ?> props) {
        if (props == null) {
            props = new Hashtable<String, Object>();
        }
        path = getProperty(props, PATH);
    }

    public String getPath() {
        return path;
    }

    public boolean isFilled() {
        return !empty(path);
    }

    @Override
    public String toString() {
        return "FileContentResolverConfig{" +
            "path='" + path + '\'' +
            "} ";
    }
}
