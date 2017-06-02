package nl.hu.fotoalbum;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class App {

	@GET
	@RolesAllowed("admin")
	@Produces(MediaType.TEXT_HTML)
	public String getPage() { 

	
		return "<h1>app</h1>";
	}
}
