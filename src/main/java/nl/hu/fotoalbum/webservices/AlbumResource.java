package nl.hu.fotoalbum.webservices;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.User;
import nl.hu.fotoalbum.services.ServiceProvider;

@Path("/album")
public class AlbumResource {
	//Initialize json object mapper
	private ObjectMapper mapper = new ObjectMapper();

	@POST
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postAlbum(@FormParam("title") String title, @FormParam("description") String description,
			@FormParam("shareType") String shareType, @FormParam("sharedusers") String sharedUsers,
			@Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		// Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();

		Album a = new Album(title, description, shareType, ServiceProvider.getUserService().getByEmail(email));

		if (shareType.equals("U")) {
			a.setSharedUserIds(getSharedUsers(sharedUsers));
		}

		int albumId = ServiceProvider.getAlbumService().save(a);

		a = ServiceProvider.getAlbumService().get(albumId);

		return Response.ok(mapper.writeValueAsString(a)).build();
	}

	@GET
	@RolesAllowed("user")
	@Path("{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbum(@PathParam("code") String code, @Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();
		
		Album a = ServiceProvider.getAlbumService().getByCode(code);
		
		if(a == null) return Response.status(Response.Status.NOT_FOUND).build();

		return Response.ok(albumToJson(a, email)).build();
	}

	@PUT
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAlbum(@FormParam("code") String code, @FormParam("title") String title,
			@FormParam("description") String description, @FormParam("shareType") String shareType,
			@Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		Album a = ServiceProvider.getAlbumService().getByCode(code);

		// Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();

		if (!a.getUser().getEmail().equals(email)) return Response.status(Response.Status.UNAUTHORIZED).build();

		a.setTitle(title);
		a.setDescription(description);
		a.setShareType(shareType);

		ServiceProvider.getAlbumService().saveOrUpdate(a);

		return Response.ok(mapper.writeValueAsString(a)).build();
	}

	@DELETE
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAlbum(@PathParam("albumcode") String code) throws JsonProcessingException {
		Album a = ServiceProvider.getAlbumService().getByCode(code);

		ServiceProvider.getAlbumService().delete(a);

		return Response.ok("deleted").build();
	}

	@GET
	@RolesAllowed("user")
	@Path("public")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPublicAlbums() throws JsonProcessingException {

		return Response.ok(albumsToJson(ServiceProvider.getAlbumService().getPublic())).build();
	}
	
	@GET
	@RolesAllowed("user")
	@Path("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserAlbums(@Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();
		
		User u = ServiceProvider.getUserService().getByEmail(email);
		
		return Response.ok(albumsToJson(ServiceProvider.getAlbumService().getFromUser(u.getId()))).build();
	}

	private Set<Integer> getSharedUsers(String sharedUsers) {
		ObjectMapper mapper = new ObjectMapper();

		JsonNode sharedUsersNode = null;
		Set<Integer> sharedUsersSet = new HashSet<Integer>();

		try {
			sharedUsersNode = mapper.readTree(sharedUsers);
			for (int i = 0; i < sharedUsersNode.size(); i++) {
				User u = ServiceProvider.getUserService().getByEmail(sharedUsersNode.get(i).asText());
				if (u != null)
					sharedUsersSet.add(u.getId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sharedUsersSet;
	}

	private String albumsToJson(List<Album> albums) {
		ArrayNode albumsNode = mapper.createArrayNode();

		for (Album a : albums) {
			ObjectNode albumNode = mapper.convertValue(a, ObjectNode.class);

			ArrayNode pictures = mapper.valueToTree(a.getPictures());

			albumNode.putArray("pictures").addAll(pictures);

			albumNode.putPOJO("user", a.getUser());

			albumsNode.add(albumNode);
		}

		try {
			return mapper.writeValueAsString(albumsNode);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return "";
	}

	private String albumToJson(Album a, String email) {
		ObjectNode albumNode = mapper.convertValue(a, ObjectNode.class);

		ArrayNode pictures = mapper.valueToTree(a.getPictures());

		albumNode.putArray("pictures").addAll(pictures);
		
		albumNode.putPOJO("user", a.getUser());
		
		albumNode.put("isFromUser", a.getUser().getEmail().equals(email));
		
		try {
			System.out.println(mapper.writeValueAsString(albumNode));
			return mapper.writeValueAsString(albumNode);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return "";
	}
}
