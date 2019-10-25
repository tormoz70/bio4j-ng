package ru.bio4j.ng.model.transport;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public class BioError extends RuntimeException {

    protected int errorCode = 6000;

    public BioError() {
        super();
    }

    public BioError(int code) {
        super();
        errorCode = code;
    }

    public BioError(int code, String message) {
        super(message);
        errorCode = code;
    }
    public BioError(String message) {
        super(message);
    }
    public BioError(int code, String message, Exception e) {
        super(message, e);
        errorCode = code;
    }
    public BioError(String message, Exception e) {
        super(message, e);
    }

    public BioError(int code, Exception e) {
        super(e);
        errorCode = code;
    }
    public BioError(Exception e) {
        super(e);
    }

    public static BioError wrap(Exception e) {
        if(e != null) {
            if (e instanceof BioError)
                return (BioError) e;
            return new BioError(e);
        }
        return null;
    }

    public int getErrorCode() {
        return errorCode;
    }

    //********************************************************************************


    @JsonTypeInfo(use = Id.CLASS, property = "@class")
    public static class BadRequestType extends BioError {

        public BadRequestType() {
            super(6400);
        }
        public BadRequestType(String requestType) {
            super(6400, String.format("Value of argument \"requestType\":\"%s\" is unknown!", requestType));
        }
        public BadRequestType(String msgTemplate, String requestType) {
            super(6400, String.format(msgTemplate, requestType));
        }
    }

    @JsonTypeInfo(use = Id.CLASS, property = "@class")
    public static class MethodNotAllowed extends BioError {
        public MethodNotAllowed() {
            super(6405, "Метод не доступен!");
        }
    }

    @JsonTypeInfo(use = Id.CLASS, property = "@class")
    public static class MethodNotImplemented extends BioError {
        public MethodNotImplemented() {
            super(6501, "Метод не реализован!");
        }
    }

    @JsonTypeInfo(use = Id.CLASS, property = "@class")
    public static abstract class Login extends BioError {
        public Login(int code) {
            super(code);
        }
        public Login(int code, String message) {
            super(code, message);
        }

        @JsonTypeInfo(use = Id.CLASS, property = "@class")
        public static class Unauthorized extends BioError.Login {
            public Unauthorized() {
                super(6401, "Не верное имя или пароль пользователя!");
            }
        }

        @JsonTypeInfo(use = Id.CLASS, property = "@class")
        public static class Forbidden extends BioError.Login {
            public Forbidden() {
                super(6403, "Доступ запрещен!");
            }
        }

    }

    @JsonTypeInfo(use = Id.CLASS, property = "@class")
    public static class BadIODescriptor extends BioError {
        public BadIODescriptor() {
            super();
        }
        public BadIODescriptor(String message) {
            super(message);
        }
    }

}
