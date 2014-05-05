package ru.bio4j.ng.service.api;

/**
 * Our business service. 
 */
public interface DataProvider extends BioService {

    String getData(final String json) throws Exception;

}
