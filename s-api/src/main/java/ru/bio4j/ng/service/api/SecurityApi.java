package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SecurityApi {
    void init(SecurityService securityService);
    User doGetUser(final HttpServletRequest request);
    User doLogin(final HttpServletRequest request);
    void doLogoff(final HttpServletRequest request);
}
