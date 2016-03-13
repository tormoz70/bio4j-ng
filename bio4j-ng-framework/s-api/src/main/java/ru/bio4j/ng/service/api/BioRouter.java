package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface BioRouter {
//    public static interface Callback {
//        public void run(BioRespBuilder.Builder brsp) throws Exception;
//    }
    void route(final HttpServletRequest request, final HttpServletResponse response) throws Exception;
}
