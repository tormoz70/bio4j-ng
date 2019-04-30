package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioEventAttrs;

/**
 * Created by ayrat on 04.05.14.
 */
public interface EventHandler {
    void handle(BioService sender, BioEventAttrs attrs) throws Exception;
}
