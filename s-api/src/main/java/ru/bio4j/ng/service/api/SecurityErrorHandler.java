package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioError;

import javax.servlet.http.HttpServletResponse;

public interface SecurityErrorHandler {
    boolean writeError(BioError.Login exception, HttpServletResponse response);
}
