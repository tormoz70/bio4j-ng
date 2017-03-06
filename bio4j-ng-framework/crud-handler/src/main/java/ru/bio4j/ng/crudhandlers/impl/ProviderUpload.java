package ru.bio4j.ng.crudhandlers.impl;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.database.api.BioCursor;
import ru.bio4j.ng.database.api.SQLAction;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLStoredProc;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.FCloudProvider;
import ru.bio4j.ng.service.api.FileSpec;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by ayrat on 07.03.2016.
 */
public class ProviderUpload extends ProviderAn {

    private FCloudProvider fcloudProvider;
    public ProviderUpload(FCloudProvider fcloudProvider) {
        this.fcloudProvider = fcloudProvider;
    }

    private static void writeResponse(String json, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.append(json);
    }

    private static void writeResult(List<FileSpec> files, HttpServletResponse response) throws IOException {
        String json = Jsons.encode(files);
        writeResponse(json, response);
    }

    public void processUpload(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        Collection<Part> parts = null;
        try {
            parts = request.getParts();
        } catch (Exception e) {}
        if(parts != null) {
            //LOG.debug("Parts recived: {}", parts.size());
            List<FileSpec> files = new ArrayList<>();
            for (Part p : parts) {
                FileSpec file = fcloudProvider.regFile(
                        request.getParameter("uplduid"),
                        Httpc.extractFileNameFromPart(p),
                        p.getInputStream(),
                        p.getSize(),
                        p.getContentType(),
                        request.getRemoteHost(),
                        request.getParameter("adesc")
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

    @Override
    public void process(final BioRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process postDataSet for \"{}\" request...", request.getBioCode());
    }

}
