package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioError;

import javax.servlet.http.HttpServletResponse;

public interface SecurityErrorHandler {
    /***
     * Обработчик ошибок аутентификации
     * @param exception
     * @param response
     * @return если вернуть true, то будет продолжена обработка запроса, иначе выход
     * @throws Exception
     */
    boolean writeError(BioError.Login exception, HttpServletResponse response);
}
