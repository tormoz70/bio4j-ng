package ru.bio4j.ng.service.api;

public interface BioConfigurable<T>  {
    public T getConfig();
    public boolean configIsReady();

}
