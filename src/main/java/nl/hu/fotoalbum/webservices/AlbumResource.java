package nl.hu.fotoalbum.webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.Part;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;
import nl.hu.fotoalbum.persistence.Picture;
import nl.hu.fotoalbum.services.ServiceProvider;

@Path("/album")
public class AlbumResource {

	// @RolesAllowed("user")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String postAlbum(@FormParam("title") String title, @FormParam("description") String description,
			@FormParam("shareType") String shareType, @FormDataParam("pictures") FormDataBodyPart pictureParts) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		Album a = new Album(title, description, shareType, ServiceProvider.getUserService().get(1));

		int albumId = ServiceProvider.getAlbumService().save(a);

		a = ServiceProvider.getAlbumService().get(albumId);

		/*for (BodyPart i : pictureParts.getParent().getBodyParts()) {
			
			ContentDisposition content = i.getContentDisposition();
			
			Picture p = new Picture(a, content.getType());

			int pictureId = ServiceProvider.getPictureService().save(p);

			p = ServiceProvider.getPictureService().get(pictureId);

			String name = p.getCode() + "." + p.getType();

			try (OutputStream outPut = new FileOutputStream(
					new File("C:\\Users\\Stefan\\Documents\\School\\WAC\\test" + name));
					InputStream fileContent = i.getEntityAs(InputStream.class)) {
				
				int read = 0;
				final byte[] bytes = new byte[1024];

				while ((read = fileContent.read(bytes)) != -1) {
					outPut.write(bytes, 0, read);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}*/

		return mapper.writeValueAsString(a);
	}

	@GET
	@Path("{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAlbum(@PathParam("code") String code) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		Album a = ServiceProvider.getAlbumService().get(id);
		
		System.out.println(ServiceProvider.getPictureService().getNextId(a));

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
