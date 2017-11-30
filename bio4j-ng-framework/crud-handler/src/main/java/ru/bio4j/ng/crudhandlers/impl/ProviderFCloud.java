package ru.bio4j.ng.crudhandlers.impl;

import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.FCloudProvider;
import ru.bio4j.ng.service.api.FileSpec;
import ru.bio4j.ng.service.api.SrvcUtils;
import ru.bio4j.ng.service.types.BioWrappedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ayrat on 07.03.2016.
 */
public class ProviderFCloud extends ProviderAn<BioRequestFCloud> {

    private FCloudProvider fcloudProvider;
    public ProviderFCloud(FCloudProvider fcloudProvider) {
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

    private void runImport(final BioRequestFCloud request, final HttpServletResponse response) throws Exception {
        fcloudProvider.getApi().runImport(request.getUser());
        BioRespBuilder.DataBuilder responseBuilder = BioRespBuilder.dataBuilder().exception(null);
        response.getWriter().append(responseBuilder.json());
    }

     private void processUpload(final BioRequestFCloud request, final HttpServletResponse response) throws Exception {
        Collection<Part> parts = null;
        try {
            parts = request.getHttpRequest().getParts();
        } catch (Exception e) {}
        if(parts != null) {
            //LOG.debug("Parts recived: {}", parts.size());
            List<FileSpec> files = new ArrayList<>();
            for (Part p : parts) {
                String fileName = Httpc.extractFileNameFromPart(p);
                FileSpec file = fcloudProvider.getApi().regFile(
                        request.getUploadUid(),
                        fileName,
                        p.getInputStream(),
                        p.getSize(),
                        p.getContentType(),
                        request.getRemoteIP(),
                        request.getUploadDesc(),
                        request.getExtParam(),
                        request.getUser()
                );
                if(file != null)
                    files.add(file);
            }
            writeResult(files, response);
        } else
            writeResponse("[]", response);
    }

    @Override
    public void process(final BioRequestFCloud request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process postDataSet for \"{}\" request...", request.getBioCode());


        FCloudCommand fcmd = request.getCmd();
        if(fcmd != null) {
            if(fcmd == FCloudCommand.UPLOAD)
                processUpload(request, response);
            if(fcmd == FCloudCommand.RUNIMPORT)
                runImport(request, response);
        }
    }

}
