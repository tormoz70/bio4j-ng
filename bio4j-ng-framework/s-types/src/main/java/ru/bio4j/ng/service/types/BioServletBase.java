package ru.bio4j.ng.service.types;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.ConfigProvider;
import ru.bio4j.ng.service.api.SecurityHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class BioServletBase extends HttpServlet {

    public final static String SCFG_PARAM_NAME_BIODEBUG = "bioDebug";
    public final static String SCFG_PARAM_NAME_FORWARD_URL = "forwardURL";
    public final static String SCFG_PARAM_NAME_PUBLIC_AREAS = "publicAreas";
//    public final static String QRY_PARAM_NAME_MODULE = "bm";
//    public final static String QRY_PARAM_NAME_BIOCODE = "biocd";
//    public final static String QRY_PARAM_NAME_UID = "uid";

    protected Logger LOG;

    protected boolean bioDebug = false;

    protected SecurityHandler securityHandler;
    protected ConfigProvider configProvider;

    public BioServletBase() {
        LOG = LoggerFactory.getLogger(getClass());
    }

    protected void initServices(ServletContext servletContext) throws Exception {
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

    public static void writeSuccess(BioRespBuilder.SuccessBuilder bresp, HttpServletResponse response) throws IOException {
        String brespJson = Jsons.encode(bresp);
        writeResponse(brespJson, response);
    }
    public static void writeError(BioRespBuilder.AnErrorBuilder bresp, HttpServletResponse response, boolean debugMode) throws IOException {
        if(!debugMode) {
            BioError e = bresp.getException();
            if ((e != null) && !(e instanceof BioError.Login))
                bresp.exception(new BioError("На сервере произошла непредвиденная ошибка!"));
        }
        String brespJson = Jsons.encode(bresp);
        writeResponse(brespJson, response);
    }

    protected void responseError(BioError error, HttpServletResponse response) throws IOException {
        writeError(BioRespBuilder.anErrorBuilder()
                .exception(error), response, bioDebug);

    }
}
