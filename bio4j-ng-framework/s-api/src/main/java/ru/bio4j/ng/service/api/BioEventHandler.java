package ru.bio4j.ng.service.api;

/**
 * Created by ayrat on 04.05.14.
 */
public interface BioEventHandler {
    void handle(BioService sender, BioEventAttrs attrs) throws Exception;
}
