package ru.bio4j.ng.service.api;

/**
 * Created by ayrat on 08.05.14.
 */
public interface ConfigProvider {
    public BioConfig getConfig();
    public boolean configIsRedy();
}
