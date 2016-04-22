package ru.bio4j.ng.remote.api.http;

import ru.bio4j.ng.service.types.BioWrappedRequest;
import ru.bio4j.ng.service.types.WarSecurityFilterBase;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ProxyFilter extends WarSecurityFilterBase implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        BioWrappedRequest rereq = new BioWrappedRequest(req);
        rereq.putHeader("Access-Control-Allow-Origin", "*");
        super.doFilter(rereq, response, chain);
    }

}
