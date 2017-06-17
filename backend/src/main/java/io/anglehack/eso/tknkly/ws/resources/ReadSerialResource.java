package io.anglehack.eso.tknkly.ws.resources;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.anglehack.eso.tknkly.models.MotionDataObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by root on 6/17/17.
 */
@Path("/serial")
public class ReadSerialResource {

    private Multimap<String, MotionDataObject> multimap;

    public ReadSerialResource() {
        this.multimap = ArrayListMultimap.create();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataAll() {
        return Response.ok().entity(multimap).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getData(@PathParam("id") String id) {
        return Response.ok().entity(multimap.get(id)).build();
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postData(@PathParam("id") String id,
                             List<MotionDataObject> data) {
        multimap.putAll(id,data);
        return Response.ok().build();
    }

    @POST
    @Path("single/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postDataSingle(@PathParam("id") String id,
                                   MotionDataObject data) {
        multimap.put(id,data);
        return Response.ok().build();
    }

}
