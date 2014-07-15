package ru.bio4j.ng.model.transport;

/**
 * Created by ayrat on 07.06.14.
 */
public class BioError extends Exception {
    public BioError() {
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
        public BadRequestType(String requestType) {
            super(String.format("Value of argument \"requestType\":\"%s\" is unknown!", requestType));
        }
    }

    public static class Login extends BioError {
        public static class BadLogin extends BioError.Login { }
        public static class LoginExpired extends BioError.Login { }
    }
}
