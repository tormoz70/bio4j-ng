package ru.bio4j.ng.service.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

public interface ErrorWriter {
    boolean write(Exception exception, HttpServletResponse response, Boolean debugMode) throws Exception;
}
