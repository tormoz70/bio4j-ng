package ru.bio4j.ng.router;

import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.service.api.BioRouter;

/**
 * Created by ayrat on 08.05.14.
 */
public interface BioRouteHandler<T extends BioRequest> {
    void handle(T request, BioRouter.Callback callback) throws Exception;
}
