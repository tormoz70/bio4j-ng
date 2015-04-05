package ru.bio4j.ng.rapi.restful.ims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import ru.bio4j.collections.Parameter;
//import ru.bio4j.model.transport.BioRequest;
//import ru.bio4j.model.transport.BioResponse;
//import ru.bio4j.model.transport.jstore.BioResponseJStore;
//import ru.bio4j.model.transport.jstore.Sort;
//import ru.bio4j.model.transport.jstore.StoreData;
//import ru.bio4j.service.processing.QueryProcessor;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Collections;

@Path("/test")
public class UIRestService {

    private static Logger LOG = LoggerFactory.getLogger(UIRestService.class);

    @Context
    private ServletContext servletContext;

//    @POST
//    @Path("/sproc")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public BioResponse getPage(BioRequest bioRequest) {
//        final QueryProcessor queryProcessor= (QueryProcessor) servletContext.getAttribute(QueryProcessor.class.getName());
//        final BioResponseJStore bioResponse = new BioResponseJStore();
//        bioResponse.setBioCode(bioRequest.getBioCode());
//        try {
//            final StoreData read = queryProcessor.read(bioRequest, Collections.<String, Parameter>emptyMap());
//            bioResponse.setPacket(read);
//            bioResponse.setSuccess(true);
//        } catch (Exception e) {
//            LOG.error("Can't process request", e);
//            bioResponse.setExceptions(Collections.<Exception>singletonList(e));
//        }
//        return bioResponse;
//    }


//    @POST
//    @Path("/example")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public BioResponse getExamplePage(BioRequest bioRequest) {
//        final QueryProcessor queryProcessor= (QueryProcessor) servletContext.getAttribute(QueryProcessor.class.getName());
//        final BioResponseJStore bioResponse = new BioResponseJStore();
//        bioResponse.setBioCode(bioRequest.getBioCode());
//        try {
//            final StoreData read = queryProcessor.read(bioRequest, Collections.<String, Parameter>emptyMap());
//            bioResponse.setPacket(read);
//            bioResponse.setSuccess(true);
//        } catch (Exception e) {
//            LOG.error("Can't process request", e);
//            bioResponse.setExceptions(Collections.<Exception>singletonList(e));
//        }
//        return bioResponse;
//    }

    @GET
    @Path("/status")
    public String getStatus() {
        return "BuGoGa";
    }

//    @GET
//    @Path("/stub")
//    @Produces("application/json")
//    public BioResponse stub() {
//        BioResponseJStore response = new BioResponseJStore();
//        response.setBioCode("bugoga");
//        Sort sort = new Sort();
//        sort.add("field", Sort.Direction.ASC);
//        response.setSort(sort);
//        return response;
//    }

}
