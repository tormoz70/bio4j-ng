package ru.bio4j.ng.model.transport;

import java.util.List;

/**
 * Базовый класс для всех ответов сервера
 */
public class AjaxResponse {

    /**
     * Содержит true если все при обработке запроса не произошло ни каких ошибок
     */
    private boolean success;

    /**
     * Если на сервере произошди ошибки при обработке запроса, то здесь возвращается коллекция объектов содержащий ошибки
     * Их может быть много в общем случае
     */
    private List<Exception> exceptions;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

}
