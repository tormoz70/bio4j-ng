package ru.bio4j.ng.fcloud.restful;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.FileSpec;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Path("/fcloud")
public class FCloudSrvc extends RestSrvcBase {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileSpec> getList(@Context HttpServletRequest request) throws Exception {
        return _getList(request);
    }

    @GET
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    public StoreMetadata getMetadata(@Context HttpServletRequest request) throws Exception {
        return _getMetadata();
    }


    @GET
    @Path("/{id}")
    public Response downloadFile()
    {
        StreamingOutput fileStream =  new StreamingOutput()
        {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException
            {
                try
                {
                    java.nio.file.Path path = Paths.get("C:/temp/test.pdf");
                    byte[] data = Files.readAllBytes(path);
                    output.write(data);
                    output.flush();
                }
                catch (Exception e)
                {
                    throw new WebApplicationException("File Not Found !!");
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = myfile.pdf")
                .build();
    }

//    @Path("/files")
//    @POST
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response uploadFiles2(@DefaultValue("") @FormDataParam("tags") String tags,
//                                 @FormDataParam("files") List<FormDataBodyPart> bodyParts,
//                                 @FormDataParam("files") FormDataContentDisposition fileDispositions) {
//        List<ABean> abeans = exctarctBean(request);
//        return _save(bioCode, abeans, request);
//
//    }

//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<ABean> update(@Context HttpServletRequest request) throws Exception {
//        List<ABean> abeans = exctarctBean(request);
//        return _save(bioCode, abeans, request);
//    }

//    @DELETE
//    @Path("/{ids}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public void delete(@PathParam("ids") String ids, @Context HttpServletRequest request) throws Exception {
//        List<Object> idsList = parsIds(ids, Long.class);
//        _delete(bioCode, idsList, request);
//    }

}
