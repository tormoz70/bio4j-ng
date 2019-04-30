package ru.bio4j.ng.service.api;

public interface Configurable<T>  {
    public T getConfig();
    public boolean configIsReady();

}
