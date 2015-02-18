package ru.bio4j.ng.model.transport;

public class BioError extends Exception {

    public BioError() {
        super();
    }

    public BioError(String message) {
        super(message);
    }
    public BioError(String message, Exception e) {
        super(message, e);
    }

    private BioError(Exception e) {
        super(e);
    }

    public static BioError wrap(Exception e) {
        if(e instanceof BioError)
            return (BioError)e;
        return new BioError(e);
    }

    //********************************************************************************

    public static class BadRequestType extends BioError {
        public BadRequestType() {
            super();
        }
        public BadRequestType(String requestType) {
            super(String.format("Value of argument \"requestType\":\"%s\" is unknown!", requestType));
        }
    }

    public static class LacationFail extends BioError {
        public LacationFail() {
            super();
        }
        public LacationFail(Object locationId) {
            super(String.format("Cursor fail location to [%s] record by pk!!!", locationId));
        }
    }

    public static abstract class Login extends BioError {
        public Login() {
            super();
        }
        public Login(String message) {
            super(message);
        }

        public static class BadLogin extends BioError.Login {
            public BadLogin() {
                super("Не верное имя или пароль пользователя!");
            }
            public BadLogin(String message) {
                super(message);
            }
        }
        public static class LoginExpired extends BioError.Login {
            public LoginExpired() {
                super("Сеанс связи прекращен сервером!");
            }
            public LoginExpired(String message) {
                super(message);
            }
        }
        public static class LoginGet extends BioError.Login {
            public LoginGet() {
                super("Введите имя и пароль пользователя!");
            }
        }
    }

    public static class BadIODescriptor extends BioError {
        public BadIODescriptor() {
            super();
        }
        public BadIODescriptor(String message) {
            super(message);
        }
    }

}
