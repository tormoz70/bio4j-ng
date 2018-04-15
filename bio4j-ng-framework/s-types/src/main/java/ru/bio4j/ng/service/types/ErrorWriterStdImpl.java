package ru.bio4j.ng.service.types;

import ru.bio4j.ng.service.api.ErrorWriter;

import javax.servlet.http.HttpServletResponse;

public class ErrorWriterStdImpl implements ErrorWriter {

    @Override
    public void write(Exception exception, HttpServletResponse response) {
        
    }
}
