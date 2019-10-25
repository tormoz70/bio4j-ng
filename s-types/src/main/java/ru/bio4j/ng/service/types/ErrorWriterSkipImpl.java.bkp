package ru.bio4j.ng.service.types;

import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.api.ErrorWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

public class ErrorWriterSkipImpl implements ErrorWriter {

    @Override
    public boolean write(Exception exception, HttpServletResponse response) {
        return true;
    }
}
