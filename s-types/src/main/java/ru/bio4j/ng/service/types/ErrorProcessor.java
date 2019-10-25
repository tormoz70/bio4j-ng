package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jecksons;
import ru.bio4j.ng.commons.utils.SrvcUtils;
import ru.bio4j.ng.database.api.BioSQLApplicationError;
import ru.bio4j.ng.database.commons.DbUtils;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.LoginResult;

import javax.servlet.ServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.function.Function;

public class ErrorProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorProcessor.class);

    public static Response process(Throwable exception, Function<Throwable, Response> builder) {
        try {
            if(builder != null)
                return builder.apply(exception);

            BioError.Login loginError = exception instanceof BioError.Login ? (BioError.Login) exception : null;
            if (loginError != null) {
                LoginResult result = LoginResult.Builder.error(loginError);
                return Response.status(Response.Status.UNAUTHORIZED).entity(Jecksons.getInstance().encode(result))
                        .type(MediaType.APPLICATION_JSON).build();
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
                return Response.status(errorBean.getErrorCode()).entity(Jecksons.getInstance().encode(result))
                        .type(MediaType.APPLICATION_JSON).build();
            } else if (exception instanceof javax.ws.rs.NotAllowedException) {
                LOG.error(null, exception);
                LoginResult result = LoginResult.Builder.error(new BioError.MethodNotAllowed());
                return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(Jecksons.getInstance().encode(result))
                        .type(MediaType.APPLICATION_JSON).build();
            } else if (exception instanceof javax.ws.rs.NotFoundException) {
                LOG.error(null, exception);
                LoginResult result = LoginResult.Builder.error(new BioError.MethodNotImplemented());
                return Response.status(Response.Status.NOT_IMPLEMENTED).entity(Jecksons.getInstance().encode(result))
                        .type(MediaType.APPLICATION_JSON).build();
            }
            LOG.error(null, exception);
            LoginResult result = LoginResult.Builder.error(new BioError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Неизвестная ошибка на сервере"));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Jecksons.getInstance().encode(result))
                    .type(MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Response process(Throwable exception) {
        return process(exception, null);
    }

    public static void doResponse(Throwable exception, ServletResponse response) {
        try {
            Response rsp = ErrorProcessor.process(exception);
            response.getWriter().print(rsp.getEntity());
        } catch(IOException e) {
            throw BioError.wrap(e);
        }
    }

}
