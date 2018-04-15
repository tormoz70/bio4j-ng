package ru.bio4j.ng.service.types;

import ru.bio4j.ng.service.api.ErrorWriter;

import javax.servlet.http.HttpServletResponse;

public class ErrorHandler {

    private static final ErrorHandler instance = new ErrorHandler();

    private ErrorWriter errorWriter;

    private ErrorHandler() {
        errorWriter = new ErrorWriterJsonImpl();
    }

    public static ErrorHandler getInstance(){
        return instance;
    }

    public void writeError(Exception exception, HttpServletResponse response) {
        errorWriter.write(exception, response);
    }
}
