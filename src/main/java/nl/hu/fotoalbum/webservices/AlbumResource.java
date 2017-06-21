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

import org.jsoup.Jsoup;

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

	//Save new album
	@POST
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postAlbum(@FormParam("title") String title, @FormParam("description") String description,
			@FormParam("share-type") String shareType, @FormParam("sharedusers") String sharedUsers,
			@Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		// Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();

		//Remove html tags from title and description
		title = Jsoup.parse(title).text();
		description = Jsoup.parse(description).text();
		
		//Create new album object
		Album a = new Album(title, description, shareType, ServiceProvider.getUserService().getByEmail(email));

		if (shareType.equals("U")) {
			//Set shareduserids if album sharetype is U
			a.setSharedUserIds(getSharedUsers(sharedUsers));
		}

		//Save album and get ID
		int albumId = ServiceProvider.getAlbumService().save(a);

		//Get saved album
		a = ServiceProvider.getAlbumService().get(albumId);

		//Build json string from new album
		return Response.ok(mapper.writeValueAsString(a)).build();
	}

	//Get album by code
	@GET
	@RolesAllowed("user")
	@Path("{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbum(@PathParam("code") String code, @Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		//Get album by code
		Album a = ServiceProvider.getAlbumService().getByCode(code);
		
		//Return 404 if album isn't found
		if(a == null) return Response.status(Response.Status.NOT_FOUND).build();
		
		//Get email from current user
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();
		
		//Get user by email
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

		//Build json string from found album
		return Response.ok(albumToJson(a, email)).build();
	}

	
	//Update album
	@PUT
	@RolesAllowed("user")
	@Path("{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAlbum(@PathParam("code") String code, @FormParam("title") String title,
			@FormParam("description") String description, @FormParam("shareType") String shareType,
			@FormParam("sharedusers") String sharedUsers,
			@Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		//Get album by code
		Album a = ServiceProvider.getAlbumService().getByCode(code);
		
		//Return 404 if album isn't found
		if(a == null) return Response.status(Response.Status.NOT_FOUND).build();

		//Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();

		//Check if user is the owner of the album
		if (!a.getUser().getEmail().equals(email)) return Response.status(Response.Status.UNAUTHORIZED).build();
		
		//Remove html tags from title and description
		title = Jsoup.parse(title).text();
		description = Jsoup.parse(description).text();

		//Set new values
		a.setTitle(title);
		a.setDescription(description);
		a.setShareType(shareType);
		
		if (shareType.equals("U")) {
			//Set shareduserids if album sharetype is U
			a.setSharedUserIds(getSharedUsers(sharedUsers));
		}else{
			//Remove shareduserids if album sharetype isn't U
			a.setSharedUserIds(new HashSet<Integer>());
		}

		//Update album
		ServiceProvider.getAlbumService().saveOrUpdate(a);

		//Build json string from saved album
		return Response.ok(albumToJson(a, email)).build();
	}

	//Delete album
	@DELETE
	@RolesAllowed("user")
	@Path("{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAlbum(@PathParam("code") String code, @Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		//Get album by code
		Album a = ServiceProvider.getAlbumService().getByCode(code);
		
		//Return 404 if album isn't found
		if(a == null) return Response.status(Response.Status.NOT_FOUND).build();
		
		//Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();

		//Check if user is the owner of the album
		if (!a.getUser().getEmail().equals(email)) return Response.status(Response.Status.UNAUTHORIZED).build();

		//Delete album
		ServiceProvider.getAlbumService().delete(a);
		
		//Create json response
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("response", "deleted");

		//Return json response
		return Response.ok(mapper.writeValueAsString(responseNode)).build();
	}

	//Get public albums
	@GET
	@RolesAllowed("user")
	@Path("public")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPublicAlbums() throws JsonProcessingException {
		//Return json string with public albums
		return Response.ok(albumsToJson(ServiceProvider.getAlbumService().getPublic())).build();
	}
	
	//Get shared albums
	@GET
	@RolesAllowed("user")
	@Path("shared")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbumsSharedWithUser(@Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		//Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();
		
		//Get user by email
		User u = ServiceProvider.getUserService().getByEmail(email);
		
		//Return json string with albums shared with user
		return Response.ok(albumsToJson(ServiceProvider.getAlbumService().getSharedWithUser(u))).build();
	}
	
	@GET
	@RolesAllowed("user")
	@Path("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserAlbums(@Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		//Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();
		
		//Get user by email
		User u = ServiceProvider.getUserService().getByEmail(email);
		
		//Return albums owned by user
		return Response.ok(albumsToJson(ServiceProvider.getAlbumService().getFromUser(u.getId()))).build();
	}

	//Function to parse json string for shared users
	private Set<Integer> getSharedUsers(String sharedUsers) {
		//Init variables
		JsonNode sharedUsersNode = null;
		Set<Integer> sharedUsersSet = new HashSet<Integer>();

		//Try to read json string and build sharedUserSet
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

		//Return shared users set
		return sharedUsersSet;
	}

	//Albums to json function
	private String albumsToJson(List<Album> albums) {
		//Create new ArrayNode
		ArrayNode albumsNode = mapper.createArrayNode();

		//Loop through albums
		for (Album a : albums) {
			//Create new ObjectNode
			ObjectNode albumNode = mapper.convertValue(a, ObjectNode.class);
			
			//Get albums pictures and add to ArrayNode
			ArrayNode pictures = mapper.valueToTree(a.getPictures());

			//Add pictures to album
			albumNode.putArray("pictures").addAll(pictures);

			//Add user to album
			albumNode.putPOJO("user", a.getUser());

			//Add album to ArrayNode
			albumsNode.add(albumNode);
		}

		try {
			//Try to return json string
			return mapper.writeValueAsString(albumsNode);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		//Return empty string if something went wrong
		return "";
	}
	
	//Album to json function
	private String albumToJson(Album a, String email) {
		//Create new ObjectNode
		ObjectNode albumNode = mapper.convertValue(a, ObjectNode.class);

		//Get albums pictures and add to ArrayNode
		ArrayNode pictures = mapper.valueToTree(a.getPictures());

		//Add pictures to album
		albumNode.putArray("pictures").addAll(pictures);
		
		//Add user to album
		albumNode.putPOJO("user", a.getUser());
		
		//Add boolean if album is from user
		albumNode.put("isFromUser", a.getUser().getEmail().equals(email));
		
		//Get shared users from album
		if(a.getUser().getEmail().equals(email) && a.getShareType().equals("U")){		
			ArrayNode sharedUsers = mapper.valueToTree(ServiceProvider.getAlbumService().getSharedUsers(a));

			albumNode.putArray("sharedUsers").addAll(sharedUsers);
		}
		
		try {
			//Try to return json string
			return mapper.writeValueAsString(albumNode);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		//Return empty string if something went wrong
		return "";
	}
}
