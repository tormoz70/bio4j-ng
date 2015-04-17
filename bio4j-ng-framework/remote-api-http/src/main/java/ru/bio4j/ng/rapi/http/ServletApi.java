package ru.bio4j.ng.rapi.http;

import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.api.ConfigProvider;
import ru.bio4j.ng.service.types.BioServletApiBase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletApi extends BioServletApiBase {

    private RemoteAPIService owner;
    public ServletApi(RemoteAPIService owner) {
        this.owner = owner;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpServletRequest req = request;
        final HttpServletResponse resp = response;
        resp.setCharacterEncoding("UTF-8");
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
