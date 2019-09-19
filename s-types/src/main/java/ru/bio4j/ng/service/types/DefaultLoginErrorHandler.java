package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioConfig;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.api.ErrorWriter;
import ru.bio4j.ng.service.api.LoginErrorHandler;

import javax.servlet.http.HttpServletResponse;

public class DefaultLoginErrorHandler implements LoginErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultLoginErrorHandler.class);

    @Override
    public boolean writeError(BioError.Login exception, HttpServletResponse response) {
        if(exception instanceof BioError.Login) {
            LOG.error("Authentication error (Level-0)!", exception);
        } else {
            LOG.error("Unexpected error while filtering (Level-1)!", exception);
        }

        ErrorWriter errorWriter = ErrorWriterType.Skip.createImpl();
        return errorWriter.write(exception, response);
    }
}
