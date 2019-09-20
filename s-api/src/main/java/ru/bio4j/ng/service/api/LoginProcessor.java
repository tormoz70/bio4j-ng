package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioConfig;
import ru.bio4j.ng.model.transport.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface LoginProcessor {
    void init(BioConfig config, SecurityApi securityApi);
    void process(final ServletRequest request, final ServletResponse response, final FilterChain chain);
}
