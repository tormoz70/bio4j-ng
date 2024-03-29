package ru.bio4j.ng.service.types;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.service.api.ConfigProvider;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

public class ServletBase extends HttpServlet {

//    public final static String SCFG_PARAM_NAME_BIODEBUG = "bioDebug";
//    public final static String SCFG_PARAM_NAME_PUBLIC_AREAS = "publicAreas";

    protected Logger LOG;

//    protected SecurityProvider securityProvider;
    protected ConfigProvider configProvider;

    public ServletBase() {
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
//        if(securityProvider == null) {
//            try {
//                securityProvider = Utl.getService(servletContext, SecurityProvider.class);
//            } catch (IllegalStateException e) {
//                securityProvider = null;
//            }
//        }
//        if(securityProvider == null)
//            throw new IllegalArgumentException("SecurityHandler not defined!");
        if(configProvider == null)
            throw new IllegalArgumentException("ConfigProvider not defined!");
    }

//    private static void writeResponse(String brespJson, HttpServletResponse response) throws IOException {
//        PrintWriter writer = response.getWriter();
//        writer.append(brespJson);
//    }

//    public static void writeError(BioRespBuilder.AnErrorBuilder bresp, HttpServletResponse response, boolean debugMode) throws IOException {
//        if(!debugMode) {
//            BioError e = bresp.getException();
//            if ((e != null) && !(e instanceof BioError.Login))
//                bresp.exception(new BioError("На сервере произошла непредвиденная ошибка!"));
//        }
//        writeResponse(bresp.json(), response);
//    }
//
//    protected void responseError(BioError error, HttpServletResponse response) throws IOException {
//        writeError(BioRespBuilder.anErrorBuilder()
//                .exception(error), response, bioDebug);
//    }
}
