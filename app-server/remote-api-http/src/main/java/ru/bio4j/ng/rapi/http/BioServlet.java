package ru.bio4j.ng.rapi.http;

import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.types.BioServletBase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BioServlet extends BioServletBase {

    public BioServlet(BioRouter router) {
        this.router = router;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            doRoute(request, response);
        } catch (Exception e) {
            LOG.error("Unexpected server error (Level-0)!", e);
        }
    }

}
