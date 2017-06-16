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
			@FormParam("share-type") String shareType, @FormParam("sharedusers") String sharedUsers,
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
		
		User u = ServiceProvider.getUserService().getByEmail(email);
		
		//Check if user has access to view the album if its shared with other users.
		if(a.getShareType().equals("U") && a.getUser().getId() != u.getId()){
			boolean gevonden = false;
			
			for(int id : a.getSharedUserIds()){
				if(id == u.getId()){
					gevonden = true;
					break;
				}
				
				if(!gevonden) return Response.status(Response.Status.UNAUTHORIZED).build();
					
			}
		}

		return Response.ok(albumToJson(a, email)).build();
	}

	@PUT
	@RolesAllowed("user")
	@Path("{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAlbum(@PathParam("code") String code, @FormParam("title") String title,
			@FormParam("description") String description, @FormParam("shareType") String shareType,
			@FormParam("sharedusers") String sharedUsers,
			@Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		Album a = ServiceProvider.getAlbumService().getByCode(code);

		// Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();

		if (!a.getUser().getEmail().equals(email)) return Response.status(Response.Status.UNAUTHORIZED).build();

		a.setTitle(title);
		a.setDescription(description);
		a.setShareType(shareType);
		
		if (shareType.equals("U")) {
			a.setSharedUserIds(getSharedUsers(sharedUsers));
		}else{
			a.setSharedUserIds(new HashSet<Integer>());
		}

		ServiceProvider.getAlbumService().saveOrUpdate(a);

		return Response.ok(albumToJson(a, email)).build();
	}

	@DELETE
	@RolesAllowed("user")
	@Path("{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAlbum(@PathParam("code") String code, @Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		Album a = ServiceProvider.getAlbumService().getByCode(code);
		
		// Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();

		if (!a.getUser().getEmail().equals(email)) return Response.status(Response.Status.UNAUTHORIZED).build();

		ServiceProvider.getAlbumService().delete(a);
		
		ObjectNode responseNode = mapper.createObjectNode();
		
		responseNode.put("response", "deleted");

		return Response.ok(mapper.writeValueAsString(responseNode)).build();
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
	@Path("shared")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbumsSharedWithUser(@Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();
		
		User u = ServiceProvider.getUserService().getByEmail(email);
		
		return Response.ok(albumsToJson(ServiceProvider.getAlbumService().getSharedWithUser(u))).build();
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
		
		if(a.getUser().getEmail().equals(email) && a.getShareType().equals("U")){		
			ArrayNode sharedUsers = mapper.valueToTree(ServiceProvider.getAlbumService().getSharedUsers(a));

			albumNode.putArray("sharedUsers").addAll(sharedUsers);
		}
		
		try {
			return mapper.writeValueAsString(albumNode);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return "";
	}
}
