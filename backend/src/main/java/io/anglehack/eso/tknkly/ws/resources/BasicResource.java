package io.anglehack.eso.tknkly.ws.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class BasicResource {

    @GET
    @Path("hello")
    public String sayHello() {
        return "hello";
    }
}
