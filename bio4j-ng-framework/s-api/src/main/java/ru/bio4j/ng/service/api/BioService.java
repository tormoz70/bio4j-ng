package ru.bio4j.ng.service.api;

public interface BioService<T>  {
    boolean isReady();
    public T getConfig();
    public boolean configIsReady();

}
