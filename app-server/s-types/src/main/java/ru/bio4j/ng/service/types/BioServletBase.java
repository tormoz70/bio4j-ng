package ru.bio4j.ng.service.types;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.api.ConfigProvider;
import ru.bio4j.ng.service.api.SecurityHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class BioServletBase extends HttpServlet {

    public final static String BIODEBUG_PARAM_NAME = "bioDebug";
    public final static String UID_PARAM_NAME = "uid";
    public final static String FORWARD_URL_PARAM_NAME = "forwardURL";

    protected Logger LOG;

    protected boolean bioDebug = false;

    protected SecurityHandler securityHandler;
    protected ConfigProvider configProvider;

    public BioServletBase() {
        LOG = LoggerFactory.getLogger(getClass());
    }

    protected void initServices(ServletContext servletContext) {
        if(configProvider == null) {
            try {
                configProvider = Utl.getService(servletContext, ConfigProvider.class);
            } catch (IllegalStateException e) {
                configProvider = null;
            }
        }
        if(securityHandler == null) {
            try {
                securityHandler = Utl.getService(servletContext, SecurityHandler.class);
            } catch (IllegalStateException e) {
                securityHandler = null;
            }
        }
        if(securityHandler == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        if(configProvider == null)
            throw new IllegalArgumentException("ConfigProvider not defined!");
        bioDebug = configProvider.getConfig().isBioDebug();
    }

    private static void writeResponse(String brespJson, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.append(brespJson);
    }

    public static void writeSuccess(BioRespBuilder.Success bresp, HttpServletResponse response) throws IOException {
        String brespJson = Jsons.encode(bresp);
        writeResponse(brespJson, response);
    }
    public static void writeError(BioRespBuilder.AnError bresp, HttpServletResponse response, boolean debugMode) throws IOException {
        if(!debugMode) {
            for (BioError e : bresp.getExceptions()) {
                if (!(e instanceof BioError.Login))
                    bresp.replaceError(e, new BioError("На сервере произошла непредвиденная ошибка!"));
            }
        }
        String brespJson = Jsons.encode(bresp);
        writeResponse(brespJson, response);
    }

    protected void responseError(BioError error, HttpServletResponse response) throws IOException {
        writeError(BioRespBuilder.anError()
                .addError(error), response, bioDebug);

    }
}
