package ru.bio4j.ng.model.transport;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.security.Principal;

public class LoginResult {
    private boolean success;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    private Exception exception;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    private Principal user;

    public boolean isSuccess() {
        return success;
    }

    public Exception getException() {
        return exception;
    }

    public Principal getUser() {
        return user;
    }

    public static class Builder {

        public static LoginResult success() {
            LoginResult rslt = new LoginResult();
            rslt.success = true;
            return rslt;
        }

        public static LoginResult success(Principal user) {
            LoginResult rslt = new LoginResult();
            rslt.success = true;
            rslt.user = user;
            return rslt;
        }

        public static LoginResult error(Exception error) {
            LoginResult rslt = new LoginResult();
            rslt.success = false;
            rslt.exception = error;
            return rslt;
        }
    }
}
