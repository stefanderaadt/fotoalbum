package nl.hu.fotoalbum.webservices;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;
import nl.hu.fotoalbum.services.ServiceProvider;

@Path("/album")
public class AlbumResource {

	// @RolesAllowed("user")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response postAlbum(@FormParam("title") String title, @FormParam("description") String description,
			@FormParam("shareType") String shareType) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		Album a = new Album(title, description, shareType, ServiceProvider.getUserService().get(1));

		int albumId = ServiceProvider.getAlbumService().save(a);

		a = ServiceProvider.getAlbumService().get(albumId);

		return Response.ok(mapper.writeValueAsString(a)).build();
	}

	@GET
	@Path("{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbum(@PathParam("code") String code) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		Album a = ServiceProvider.getAlbumService().getByCode(code);
		
		System.out.println(ServiceProvider.getPictureService().getNextId(a));

		return Response.ok(mapper.writeValueAsString(a)).build();
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
