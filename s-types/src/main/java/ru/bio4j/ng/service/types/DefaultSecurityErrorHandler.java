package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.api.ErrorWriter;
import ru.bio4j.ng.service.api.SecurityErrorHandler;

import javax.servlet.http.HttpServletResponse;

public class DefaultSecurityErrorHandler implements SecurityErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSecurityErrorHandler.class);

    private static final DefaultSecurityErrorHandler instance = new DefaultSecurityErrorHandler();

    private ErrorWriter errorWriter;

    private DefaultSecurityErrorHandler() {

    }

    private Boolean debugMode = false;
    public void init(String type, Boolean debugMode) {
        ErrorWriterType errorWriterType = Utl.enumValueOf(ErrorWriterType.class, type, ErrorWriterType.Skip);
        this.errorWriter = errorWriterType.createImpl();
        this.debugMode = debugMode;
    }

    public static DefaultSecurityErrorHandler getInstance(){
        return instance;
    }

    @Override
    public boolean writeError(BioError.Login exception, HttpServletResponse response) throws Exception {
        if(exception instanceof BioError.Login) {
            LOG.error("Authentication error (Level-0)!", exception);
        } else {
            LOG.error("Unexpected error while filtering (Level-1)!", exception);
        }

        if(errorWriter == null)
            errorWriter = ErrorWriterType.Skip.createImpl();
        return errorWriter.write(exception, response, debugMode);
    }
}
