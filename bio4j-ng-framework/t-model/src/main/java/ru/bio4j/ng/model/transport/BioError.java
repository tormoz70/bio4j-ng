package ru.bio4j.ng.model.transport;

public class BioError extends Exception {

    private int errCode = 6000;

    public BioError() {
        super();
    }

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
    }
    public BioError(int code, String message, Exception e) {
        super(message, e);
        errCode = code;
    }
    public BioError(String message, Exception e) {
        super(message, e);
    }

    private BioError(int code, Exception e) {
        super(e);
        errCode = code;
    }
    private BioError(Exception e) {
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

    public int getErrCode() {
        return errCode;
    }

    //********************************************************************************

    public static abstract class SysError extends BioError {
        public SysError() {
            super(6500);
        }
        public SysError(int code) {
            super(code);
        }
        public SysError(String message) {
            super(6500, message);
        }
    }

    public static abstract class AppError extends BioError {
        public AppError() {
            super(6200);
        }
        public AppError(String message) {
            super(6200, message);
        }
    }

    public static class BadRequestType extends BioError {

        public BadRequestType() {
            super(6400);
        }
        public BadRequestType(String requestType) {
            super(6400, String.format("Value of argument \"requestType\":\"%s\" is unknown!", requestType));
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
        public Login(int code) {
            super(code);
        }
        public Login(int code, String message) {
            super(code, message);
        }

        public static class BadLogin extends BioError.Login {
            public BadLogin() {
                super(6401, "Не верное имя или пароль пользователя!");
            }
        }
        public static class LoginExpired extends BioError.Login {
            public LoginExpired() {
                super(6402, "Сеанс связи не существует!");
            }
        }

        public static class UserBlocked extends BioError.Login {
            public UserBlocked() {
                super(6403, "Пользователь заблокирован!");
            }
        }

        public static class UserNotConfirmed extends BioError.Login {
            public UserNotConfirmed() {
                super(6404, "Пользователь не подтвержден!");
            }
        }

        public static class UserDeleted extends BioError.Login {
            public UserDeleted() {
                super(6405, "Пользователь удален!");
            }
        }

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
