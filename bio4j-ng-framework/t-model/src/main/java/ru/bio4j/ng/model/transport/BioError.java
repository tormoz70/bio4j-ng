package ru.bio4j.ng.model.transport;

public class BioError extends Exception {

    private int errCode;

    public BioError(int code) {
        super();
        errCode = code;
    }

    public BioError(int code, String message) {
        super(message);
        errCode = code;
    }
    public BioError(String message) {
        super(message);
        errCode = 500;
    }
    public BioError(int code, String message, Exception e) {
        super(message, e);
        errCode = code;
    }
    public BioError(String message, Exception e) {
        super(message, e);
        errCode = 500;
    }

    private BioError(int code, Exception e) {
        super(e);
        errCode = code;
    }
    private BioError(Exception e) {
        super(e);
        errCode = 500;
    }

    public static BioError wrap(Exception e) {
        if(e instanceof BioError)
            return (BioError)e;
        return new BioError(e);
    }

    public int getErrCode() {
        return errCode;
    }

    //********************************************************************************

    public static abstract class SysError extends BioError {
        public SysError() {
            super(500);
        }
        public SysError(String message) {
            super(500, message);
        }
    }

    public static abstract class AppError extends BioError {
        public AppError() {
            super(200);
        }
        public AppError(String message) {
            super(200, message);
        }
    }

    public static class BadRequestType extends BioError {

        public BadRequestType() {
            super(400);
        }
        public BadRequestType(String requestType) {
            super(400, String.format("Value of argument \"requestType\":\"%s\" is unknown!", requestType));
        }
    }

    public static class LocationFail extends SysError {
        public LocationFail() {
            super();
        }
        public LocationFail(Object locationId) {
            super(String.format("Cursor fail location to [%s] record by pk!!!", locationId));
        }
    }


    public static abstract class Login extends BioError {
        public Login() {
            super(401);
        }
        public Login(String message) {
            super(401, message);
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
                super("Сеанс связи не существует!");
            }
            public LoginExpired(String message) {
                super(message);
            }
        }

        public static class UserBlocked extends BioError.Login {
            public UserBlocked() {
                super("Пользователь заблокирован!");
            }
            public UserBlocked(String message) {
                super(message);
            }
        }

        public static class UserNotConfirmed extends BioError.Login {
            public UserNotConfirmed() {
                super("Пользователь не подтвержден!");
            }
            public UserNotConfirmed(String message) {
                super(message);
            }
        }

        public static class UserDeleted extends BioError.Login {
            public UserDeleted() {
                super("Пользователь удален!");
            }
            public UserDeleted(String message) {
                super(message);
            }
        }

//        public static class LoginGet extends BioError.Login {
//            public LoginGet() {
//                super("Введите имя и пароль пользователя!");
//            }
//        }
    }

    public static class BadIODescriptor extends SysError {
        public BadIODescriptor() {
            super();
        }
        public BadIODescriptor(String message) {
            super(message);
        }
    }

}
