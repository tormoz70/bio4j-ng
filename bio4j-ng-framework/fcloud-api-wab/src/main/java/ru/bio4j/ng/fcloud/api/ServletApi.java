package ru.bio4j.ng.fcloud.api;

import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServletApiBase;
import ru.bio4j.ng.service.types.BioWrappedRequest;
import ru.bio4j.ng.service.types.ErrorHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServletApi extends BioServletApiBase {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private FCloudProvider fcloudProvider;

    protected void initFCloudProvider(ServletContext servletContext) {
        if(fcloudProvider == null) {
            try {
                fcloudProvider = Utl.getService(servletContext, FCloudProvider.class);
            } catch (IllegalStateException e) {
                fcloudProvider = null;
            }
        }
    }

    private static void writeResponse(String json, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.append(json);
    }

    private static void writeResult(List<FileSpec> files, HttpServletResponse response) throws IOException {
        String json = Jsons.encode(files);
        writeResponse(json, response);
    }

    public void processUpload(final HttpServletRequest request, final HttpServletResponse response, final User usr) throws Exception {
        Collection<Part> parts = null;
        try {
            parts = request.getParts();
        } catch (Exception e) {}
        if(parts != null) {
            //LOG.debug("Parts recived: {}", parts.size());
            List<FileSpec> files = new ArrayList<>();
            for (Part p : parts) {
                FileSpec file = fcloudProvider.getApi().regFile(
                        request.getParameter("uplduid"),
                        Httpc.extractFileNameFromPart(p),
                        p.getInputStream(),
                        p.getSize(),
                        p.getContentType(),
                        request.getRemoteHost(),
                        request.getParameter("adesc"),
                        request.getParameter("extprm"),
                        usr
                );
                if(file != null)
                    files.add(file);
            }
            writeResult(files, response);
        } else
            writeResponse("[]", response);
        //BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().exception(null);
        //response.getWriter().append(responseBuilder.json());
    }

    public void runImport(final HttpServletRequest request, final HttpServletResponse response, final User usr) throws Exception {
        fcloudProvider.getApi().runImport(usr);
        BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().exception(null);
        response.getWriter().append(responseBuilder.json());
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");

        try {
            initServices(this.getServletContext());
            initFCloudProvider(this.getServletContext());

            BioQueryParams qprms = ((BioWrappedRequest) request).getBioQueryParams();
            User usr = this.securityProvider.getUser(qprms.stoken, qprms.remoteIP, qprms.remoteClient);
            String fcmd = request.getParameter("fcmd");
            if(!Strings.isNullOrEmpty(fcmd)) {
                if(fcmd.compareToIgnoreCase("upload") == 0)
                    processUpload(request, response, usr);
                if(fcmd.compareToIgnoreCase("runimport") == 0)
                    runImport(request, response, usr);
            }
        } catch (BioError e) {
            if(e.getErrCode() == 200)
                LOG.error("Server application error (Level-0)!", e);
            else
                LOG.error("Expected server error (Level-0)!", e);

            ErrorHandler.getInstance().writeError(e, response);
        } catch (Exception e) {
            LOG.error("Unexpected server error (Level-0)!", e);
            ErrorHandler.getInstance().writeError(BioError.wrap(e), response);
        }
    }

}
