package ru.bio4j.ng.rapi.resful.ims;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;

@Path("/api/*")
public class RESTFulService {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String returnTitle() {
        return "<p> IMS RESTFul Service</p>";
    }

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Track getTrackInJSON() {

		Track track = new Track();
		track.setTitle("Enter Sandman");
		track.setSinger("Metallica");

		return track;

	}

//	@POST
//	@Path("/post")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response createTrackInJSON(Track track) {
//
//		String result = "Track saved : " + track;
//		return Response.status(201).entity(result).build();
//
//	}

//    @GET
//    @Path("/getscript/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public ImsScriptsData getScript(@PathParam("id") String id) throws Exception {
//
//        if(id == null)
//        throw new IllegalArgumentException("Parameter \"id\" cannot be null!");
//        Class.forName("org.postgresql.Driver");
//        String url = "jdbc:postgresql://localhost/postgres?user=asconnect&password=qwe";
//        Connection conn = DriverManager.getConnection(url);
//        try {
//
//            int intId = Integer.parseInt(id);
//            PreparedStatement st = conn.prepareStatement("SELECT * FROM schscript WHERE schscriptid = ?");
//            st.setInt(1, intId);
//            ResultSet rs = st.executeQuery();
//            if (rs.next()) {
//                ImsScriptsDataFactory.ScriptDataBuilder builder = new ImsScriptsDataFactory.ScriptDataBuilder();
//                return builder.id("1").build();
//            }
//            rs.close();
//            st.close();
//
//        } catch (Exception e) {
//
//        } finally {
//            if (conn != null)
//                conn.close();
//        }
//
//        return null;
//
//    }

}