package ru.bio4j.ng.remote.api.http;

import ru.bio4j.ng.commons.collections.Pair;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.types.BioServletApiBase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

public class ServletApi extends BioServletApiBase {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpServletRequest req = request;
        final HttpServletResponse resp = response;
        resp.setCharacterEncoding("UTF-8");

        Collection<Part> c = null;
        try {
            c = req.getParts();
        } catch (Exception e) {}
        if(c != null) {
            LOG.debug("Parts recived: {}", c.size());
            for (Part p : c) {
                LOG.debug(" - part: ", p.getName());
            }
        }

        try {
            initRouter(this.getServletContext());
            initServices(this.getServletContext());
            doRoute(req, resp);
        } catch (Exception e) {
            LOG.error("Unexpected server error (Level-0)!", e);
            responseError(BioError.wrap(e), response);
        }
    }

}
