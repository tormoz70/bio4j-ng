package ru.bio4j.ng.content.impl;

import ru.bio4j.ng.commons.utils.Utl;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class FileContentResolverConfig {
    private String path;

    public boolean isFilled() {
        return !isNullOrEmpty(path);
    }

    @Override
    public String toString() {
        return "FileContentResolverConfig{" +
            "path='" + path + '\'' +
            "} ";
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = Utl.normalizePath(path);
    }
}
