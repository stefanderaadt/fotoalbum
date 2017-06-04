package nl.hu.fotoalbum.webservices;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.hibernate.SessionFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;

@Path("/hello")
public class AppResource {
	
	private static SessionFactory factory;

	@GET
	//@RolesAllowed("admin")
	@Produces(MediaType.TEXT_HTML)
	public String getPage() throws JsonProcessingException {
		AlbumDAO albumDAO = new AlbumDAO();
		ObjectMapper mapper = new ObjectMapper();

		Album a = albumDAO.get(Album.class, 3);

		return mapper.writeValueAsString(a);
	}
}
