package ru.bio4j.ng.rapi.resful.ims;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

@Path("/another")
public class AnotherResource {

    @GET @Produces("text/plain")
    public String getAnotherMessage() {
        return "Another";
    }
}