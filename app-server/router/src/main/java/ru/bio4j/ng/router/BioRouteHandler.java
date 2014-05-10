package ru.bio4j.ng.router;

import ru.bio4j.ng.service.api.BioRouter;

/**
 * Created by ayrat on 08.05.14.
 */
public interface BioRouteHandler {
    void handle(String requestType, String requestBody, BioRouter.Callback callback) throws Exception;
}
