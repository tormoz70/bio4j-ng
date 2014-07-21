package ru.bio4j.ng.rapi.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.ConfigProvider;
import ru.bio4j.ng.service.api.SecurityHandler;
import ru.bio4j.ng.service.types.BioServletLoginBase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletLogin extends BioServletLoginBase {
    private static final Logger LOG = LoggerFactory.getLogger(ServletLogin.class);

    private RemoteAPIService owner;
    public ServletLogin(RemoteAPIService owner) {
        this.owner = owner;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpServletRequest req = request;
        final HttpServletResponse resp = response;
        try {
            initServices(this.getServletContext());
            BioRespBuilder.Login bresp = doLogin(req);
            writeSuccess(bresp, resp);
        } catch (Exception e) {
            if(e instanceof BioError.Login)
                LOG.error("Expected error while login (Level-0) - {} -- {}!", e.getClass(), e.getMessage());
            else
                LOG.error("Unexpected error while login (Level-0)!", e);
            responseError(BioError.wrap(e), resp);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

}
