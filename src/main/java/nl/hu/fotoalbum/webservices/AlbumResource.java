package nl.hu.fotoalbum.webservices;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.hibernate.SessionFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;

@Path("/album")
public class AlbumResource {

	//@RolesAllowed("user")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String postAlbum() throws JsonProcessingException {
		AlbumDAO albumDAO = new AlbumDAO();
		ObjectMapper mapper = new ObjectMapper();

		Album a = albumDAO.get(Album.class, 3);

		return mapper.writeValueAsString(a);
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAlbum(@PathParam("id") int id) throws JsonProcessingException {
		AlbumDAO albumDAO = new AlbumDAO();
		ObjectMapper mapper = new ObjectMapper();

		Album a = albumDAO.get(Album.class, id);

		return mapper.writeValueAsString(a);
	}
	
	//@RolesAllowed("user")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String updateAlbum() throws JsonProcessingException {
		AlbumDAO albumDAO = new AlbumDAO();
		ObjectMapper mapper = new ObjectMapper();

		Album a = albumDAO.get(Album.class, 3);

		return mapper.writeValueAsString(a);
	}
	
	//@RolesAllowed("user")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteAlbum() throws JsonProcessingException {
		AlbumDAO albumDAO = new AlbumDAO();
		ObjectMapper mapper = new ObjectMapper();

		Album a = albumDAO.get(Album.class, 3);

		return mapper.writeValueAsString(a);
	}
}
