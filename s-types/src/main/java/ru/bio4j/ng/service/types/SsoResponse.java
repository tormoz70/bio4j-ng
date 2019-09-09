package ru.bio4j.ng.service.types;

import ru.bio4j.ng.model.transport.User;

public class SsoResponse {
    public User user;
    public boolean success;
    public Exception exception;
}
