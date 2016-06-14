package ru.bio4j.ng.service.types;


import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class BioServletApiBase extends BioServletBase {

    protected BioRouter router;

    protected void initRouter(ServletContext servletContext) {
        if(router == null) {
            try {
                router = Utl.getService(servletContext, BioRouter.class);
            } catch (IllegalStateException e) {
                router = null;
            }
        }
    }

    @Override
    protected void initServices(ServletContext servletContext) throws Exception {
        super.initServices(servletContext);
    }

    protected void doRoute(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if(router == null)
            throw new IllegalArgumentException("Router not defined!");
        router.route(request, response);
    }

}
