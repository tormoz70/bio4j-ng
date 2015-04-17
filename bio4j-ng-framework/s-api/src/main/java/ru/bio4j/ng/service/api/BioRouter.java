package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioRequest;

public interface BioRouter {
    public static interface Callback {
        public void run(BioRespBuilder.Builder brsp) throws Exception;
    }
    void route(BioRequest request, Callback callback) throws Exception;
}
