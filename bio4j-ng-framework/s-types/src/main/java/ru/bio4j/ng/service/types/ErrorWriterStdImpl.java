package ru.bio4j.ng.service.types;

import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.api.ErrorWriter;

import javax.servlet.http.HttpServletResponse;

public class ErrorWriterStdImpl implements ErrorWriter {

//422 Data Validation Failed. Ошибка при валидации одного или нескольких полей.
//404 Not Found. Не найден ресурс, запрашиваемый методом GET.
//405 Method Not Allowed. HTTP-метод не допустим. Пустое тело ответа.
//403 Forbidden. Пользователь авторизован, но ему запрещён доступ.
//401 Unauthorized. Access code недействителен
//5XX Серверные ошибки, не связанные с присылаемыми данными

    @Override
    public void write(Exception exception, HttpServletResponse response, Boolean debugMode) throws Exception {
        if(exception != null) {
            if(exception instanceof BioError.Login.Unauthorized)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            else if(exception instanceof BioError.Login.Forbidden)
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            else if(exception instanceof BioError.BadRequestType)
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            else if(exception instanceof BioError.MethodNotAllowed)
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            else
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
}
