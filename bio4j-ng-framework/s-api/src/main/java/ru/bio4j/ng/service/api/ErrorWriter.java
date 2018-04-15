package ru.bio4j.ng.service.api;

import javax.servlet.http.HttpServletResponse;

public interface ErrorWriter {
    void write(Exception exception, HttpServletResponse response);
}
