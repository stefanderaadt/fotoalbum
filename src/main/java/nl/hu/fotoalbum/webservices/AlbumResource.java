package nl.hu.fotoalbum.webservices;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;
import nl.hu.fotoalbum.persistence.User;
import nl.hu.fotoalbum.services.ServiceProvider;

@Path("/album")
public class AlbumResource {

	// @RolesAllowed("user")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response postAlbum(@FormParam("title") String title, @FormParam("description") String description,
			@FormParam("shareType") String shareType, @FormParam("sharedusers") String sharedUsers) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		
		Album a = new Album(title, description, shareType, ServiceProvider.getUserService().get(1));
		
		if (shareType.equals("U")) {
			System.out.println(shareType + sharedUsers);
			a.setSharedUserIds(getSharedUsers(sharedUsers));
		}
		
		System.out.println(a.getSharedUserIds());

		int albumId = ServiceProvider.getAlbumService().save(a);

		a = ServiceProvider.getAlbumService().get(albumId);
		
		System.out.println(a.getSharedUserIds());

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
	public Response updateAlbum(@FormParam("code") String code, @FormParam("title") String title, @FormParam("description") String description,
			@FormParam("shareType") String shareType) throws JsonProcessingException {
		Album a = ServiceProvider.getAlbumService().getByCode(code);
		
		a.setTitle(title);
		a.setDescription(description);
		a.setShareType(shareType);
		
		ServiceProvider.getAlbumService().saveOrUpdate(a);
		
		ObjectMapper mapper = new ObjectMapper();

		return Response.ok(mapper.writeValueAsString(a)).build();
	}

	// @RolesAllowed("user")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAlbum(@PathParam("albumcode") String code) throws JsonProcessingException {
		Album a = ServiceProvider.getAlbumService().getByCode(code);
		
		ServiceProvider.getAlbumService().delete(a);

		return Response.ok("deleted").build();
	}
	
	private Set<Integer> getSharedUsers(String sharedUsers){
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode sharedUsersNode = null;
		Set<Integer> sharedUsersSet = new HashSet<Integer>();
		
		try {
			sharedUsersNode = mapper.readTree(sharedUsers);
			for(int i = 0; i < sharedUsersNode.size(); i++){
				User u = ServiceProvider.getUserService().getByEmail(sharedUsersNode.get(i).asText());
				if(u != null) sharedUsersSet.add(u.getId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sharedUsersSet;
	}
}
