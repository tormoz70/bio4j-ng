package ru.bio4j.ng.remote.api;

import ru.bio4j.ng.commons.collections.Pair;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.types.BioServletApiBase;
import ru.bio4j.ng.service.types.BioServletBase;

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
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        try {
            initRouter(this.getServletContext());
            initServices(this.getServletContext());
            doRoute(request, response);
        } catch (BioError e) {
            if(e.getErrCode() == 200){
                LOG.error("Server application error (Level-0)!", e);
            }else{
                LOG.error("Expected server error (Level-0)!", e);
            }
            responseError(e, response);
        } catch (Exception e) {
            LOG.error("Unexpected server error (Level-0)!", e);
            responseError(BioError.wrap(e), response);
        }
    }

}