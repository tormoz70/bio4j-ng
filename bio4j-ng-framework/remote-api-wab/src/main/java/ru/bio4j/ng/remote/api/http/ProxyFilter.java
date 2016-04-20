package ru.bio4j.ng.remote.api.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.SecurityProvider;
import ru.bio4j.ng.service.types.BioWrappedRequest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ProxyFilter implements Filter {
    private Logger LOG;

    private void log_error(String s, Throwable e) {
        if(LOG != null)
            LOG.error(s, e);
    }

    private void debug(String s, Object... objects) {
        if(LOG != null)
            LOG.debug(s, objects);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final FilterChain chn = chain;
        final HttpServletResponse resp = (HttpServletResponse) response;
        resp.setCharacterEncoding("UTF-8");
        final HttpServletRequest req = (HttpServletRequest) request;

        try {
            BioWrappedRequest rereq = new BioWrappedRequest(req, null);
            rereq.putHeader("Access-Control-Allow-Origin", "*");
            chn.doFilter(rereq, resp);
        } catch (Exception e) {
            log_error("Unexpected error while filtering (Level-1)!", e);
        }
    }

    @Override
    public void destroy() {
        debug("Trying destroy");
    }
}
