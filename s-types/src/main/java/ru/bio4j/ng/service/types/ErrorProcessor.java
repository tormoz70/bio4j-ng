package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jecksons;
import ru.bio4j.ng.database.api.BioSQLApplicationError;
import ru.bio4j.ng.database.commons.DbUtils;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.LoginResult;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.function.Function;

public class ErrorProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorProcessor.class);

    public static class ErrorResponseEntry {
        public int code;
        public String entry;
        public String type;
    }

    public static ErrorResponseEntry createEntry(int code, String entry, String type) {
        ErrorResponseEntry rslt = new ErrorResponseEntry();
        rslt.code = code;
        rslt.entry = entry;
        rslt.type = type;
        return rslt;
    }

    public static ErrorResponseEntry process(Throwable exception, Function<Throwable, ErrorResponseEntry> builder) {
        try {
            if(builder != null)
                return builder.apply(exception);

            BioError.Login loginError = exception instanceof BioError.Login ? (BioError.Login) exception : null;
            if (loginError != null) {
                LoginResult result = LoginResult.Builder.error(loginError);
                return createEntry(Response.Status.UNAUTHORIZED.getStatusCode(), Jecksons.getInstance().encode(result), MediaType.APPLICATION_JSON);
            }
            BioError errorBean = exception instanceof BioError ? (BioError) exception : null;
            if (errorBean == null) {
                BioSQLApplicationError storedProcAppError = DbUtils.getInstance().extractStoredProcAppErrorMessage(errorBean);
                if (storedProcAppError != null)
                    errorBean = new BioError(Response.Status.BAD_REQUEST.getStatusCode(), storedProcAppError.getMessage());
            }
            if (errorBean != null) {
                LOG.error(null, exception);
                LoginResult result = LoginResult.Builder.error(errorBean);
                return createEntry(errorBean.getErrorCode(), Jecksons.getInstance().encode(result), MediaType.APPLICATION_JSON);
            } else if (exception instanceof javax.ws.rs.NotAllowedException) {
                LOG.error(null, exception);
                LoginResult result = LoginResult.Builder.error(new BioError.MethodNotAllowed());
                return createEntry(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), Jecksons.getInstance().encode(result), MediaType.APPLICATION_JSON);
            } else if (exception instanceof javax.ws.rs.NotFoundException) {
                LOG.error(null, exception);
                LoginResult result = LoginResult.Builder.error(new BioError.MethodNotImplemented());
                return createEntry(Response.Status.NOT_IMPLEMENTED.getStatusCode(), Jecksons.getInstance().encode(result), MediaType.APPLICATION_JSON);
            }
            LOG.error(null, exception);
            LoginResult result = LoginResult.Builder.error(new BioError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Неизвестная ошибка на сервере"));
            return createEntry(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Jecksons.getInstance().encode(result), MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Response process(Throwable exception) {
        ErrorResponseEntry entry = process(exception, null);
        return Response.status(entry.code).entity(entry.entry).type(entry.type).build();
    }

    public static void doResponse(Throwable exception, ServletResponse response) {
        try {
            ErrorResponseEntry rspEntry = process(exception, null);
            ((HttpServletResponse)response).setStatus(rspEntry.code);
            response.setContentType(rspEntry.type);
            response.getWriter().print(rspEntry.entry);
        } catch(IOException e) {
            throw BioError.wrap(e);
        }
    }

}
