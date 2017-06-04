package nl.hu.fotoalbum.webservices;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.hibernate.SessionFactory;

import com.google.gson.Gson;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;

@Path("/hello")
public class AppResource {
	
	private static SessionFactory factory;

	@GET
	//@RolesAllowed("admin")
	@Produces(MediaType.TEXT_HTML)
	public String getPage() { 		
		AlbumDAO albumDAO = new AlbumDAO();
		Gson gson = new Gson();
		
		albumDAO.startSession();
		Album a = albumDAO.get(Album.class, 1);
		
		String json = gson.toJson(a);
		
		albumDAO.stopSession();
		
		return json;
	}
}
