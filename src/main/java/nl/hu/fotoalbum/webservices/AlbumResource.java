package nl.hu.fotoalbum.webservices;

import java.io.InputStream;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;
import nl.hu.fotoalbum.services.ServiceProvider;

@Path("/album")
public class AlbumResource {

	// @RolesAllowed("user")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String postAlbum(@FormParam("title") String title, @FormParam("description") String description,
			@FormParam("shareType") String shareType, @FormDataParam("pictures") InputStream picturesIS,
			@FormDataParam("pictures") FormDataContentDisposition picturesDetail) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		//Album a = new Album(title, description, shareType, ServiceProvider.getUserService().get(1));

		//a = ServiceProvider.getAlbumService().save(a);

		return "hallo";//mapper.writeValueAsString(a);
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAlbum(@PathParam("id") int id) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		Album a = ServiceProvider.getAlbumService().get(id);

		return mapper.writeValueAsString(a);
	}

	// @RolesAllowed("user")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String updateAlbum() throws JsonProcessingException {
		AlbumDAO albumDAO = new AlbumDAO();
		ObjectMapper mapper = new ObjectMapper();

		Album a = albumDAO.get(Album.class, 3);

		return mapper.writeValueAsString(a);
	}

	// @RolesAllowed("user")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteAlbum() throws JsonProcessingException {
		AlbumDAO albumDAO = new AlbumDAO();
		ObjectMapper mapper = new ObjectMapper();

		Album a = albumDAO.get(Album.class, 3);

		return mapper.writeValueAsString(a);
	}
}
