package ru.bio4j.ng.rapi.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.SecurityHandler;
import ru.bio4j.ng.service.types.BioServletBase;
import ru.bio4j.ng.service.types.LoginServletBase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ayrat on 01.06.14.
 */
public class BioLogin extends LoginServletBase {
    private static final Logger LOG = LoggerFactory.getLogger(BioLogin.class);

    public BioLogin(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpServletRequest req = request;
        final HttpServletResponse resp = response;
        try {
            BioRespBuilder.Login bresp = doLogin(req);
            BioServletBase.writeResponse(bresp, resp);
        } catch (Exception e) {
            BioServletBase.writeResponse(BioRespBuilder.anError()
                    .addError(BioError.wrap(e)), resp);
            LOG.error("Unexpected error while login (Level-0)!", e);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

}
