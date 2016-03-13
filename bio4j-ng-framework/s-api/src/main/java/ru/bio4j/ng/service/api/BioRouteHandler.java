package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioRequest;
import javax.servlet.http.HttpServletResponse;

public interface BioRouteHandler<T extends BioRequest> {
    void handle(T request, HttpServletResponse response) throws Exception;
}
