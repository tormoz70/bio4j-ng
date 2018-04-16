package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.ErrorWriter;

import javax.servlet.http.HttpServletResponse;

public class ErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    private static final ErrorHandler instance = new ErrorHandler();

    private ErrorWriter errorWriter;

    private ErrorHandler() {

    }

    private Boolean debugMode = false;
    public void init(String type, Boolean debugMode) {
        ErrorWriterType errorWriterType = Utl.enumValueOf(ErrorWriterType.class, type);
        this.errorWriter = errorWriterType.createImpl();
        this.debugMode = debugMode;
    }

    public static ErrorHandler getInstance(){
        return instance;
    }

    public void writeError(Exception exception, HttpServletResponse response) {
        if(errorWriter == null)
            errorWriter = ErrorWriterType.Std.createImpl();
        try {
            errorWriter.write(exception, response, debugMode);
        } catch(Exception e) {
            LOG.error("Unexpected error!!!", e);
        }
    }
}
