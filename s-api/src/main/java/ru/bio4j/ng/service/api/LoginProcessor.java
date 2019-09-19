package ru.bio4j.ng.service.api;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginProcessor {
    void init(SecurityService securityService);
//    boolean doGetUser(final HttpServletRequest request, final HttpServletResponse response) throws Exception;
//    boolean doLogin(final HttpServletRequest request, final HttpServletResponse response) throws Exception;
//    boolean doLogoff(final HttpServletRequest request, final HttpServletResponse response) throws Exception;
//    boolean doOthers(final HttpServletRequest request, final HttpServletResponse response) throws Exception;
    void processLogin(final ServletRequest request, final ServletResponse response, final FilterChain chain);
}
