package ru.bio4j.ng.client.extjs.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.client.extjs.BioServlet;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.SecurityHandler;
import ru.bio4j.ng.service.types.BioServletBase;
import ru.bio4j.ng.service.types.LoginServletBase;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class LoginServlet extends LoginServletBase {
    protected final static Logger LOG = LoggerFactory.getLogger(LoginServlet.class);

    protected String forwardURL = null;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException{
        super.init(servletConfig);
        this.forwardURL = servletConfig.getInitParameter(BioServletBase.FORWARD_URL_PARAM_NAME);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            final HttpServletRequest req = request;
            final HttpServletResponse resp = response;
            initSecurityHandler(this.getServletContext());
            if(securityHandler != null) {
                BioRespBuilder.Login bresp = doLogin(req);
//                if(bresp.isSuccess())
//                    storeCurrentUsrToSession(req, bresp.getUser());
                writeResponse(bresp, resp.getWriter());
            } else {
                final String queryString = req.getQueryString();
                final String destination = this.forwardURL+(isNullOrEmpty(queryString) ? "" : "?"+queryString);
                Httpc.forwardRequest(destination, req, new Httpc.Callback() {
                    @Override
                    public void process(InputStream inputStream) throws Exception {
                        String brespJson = Utl.readStream(inputStream);
//                        BioResponse bresp = Jsons.decode(brespJson, BioResponse.class);
//                        if(bresp.isSuccess())
//                            storeCurrentUsrToSession(req, bresp.getUser());
                        writeResponse(brespJson, resp.getWriter());
                    }
                });
            }
        } catch (Exception e) {

            LOG.error("Unexpected error while login (Level-1)!", e);
        }
    }
}
