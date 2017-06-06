package nl.hu.fotoalbum.webservices;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;
import nl.hu.fotoalbum.persistence.Picture;
import nl.hu.fotoalbum.services.ServiceProvider;

@Path("/picture")
public class PictureResource {

	@GET
	@Produces("image/jpg")
	@Path("/{picturecode}")
	public Response getPicture(@PathParam("picturecode") String pictureCode) throws JsonProcessingException {

		byte[] imageData = null;
		String path = "D:\\Documents\\school\\wac\\uploads\\";
		
		System.out.println(path+pictureCode+".jpg");
		
		try (InputStream in = new FileInputStream(path+pictureCode+".jpg")) {
			BufferedImage img = ImageIO.read(in);
			
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ImageIO.write(img, "jpg", baos);
		    imageData = baos.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Response.ok(new ByteArrayInputStream(imageData)).build();
	}

	@POST
	@Path("{albumid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadPicture(@PathParam("albumid") int albumId, FormDataMultiPart multipart)
			throws JsonProcessingException {
		Map<String, List<FormDataBodyPart>> map = multipart.getFields();

		Album a = ServiceProvider.getAlbumService().get(albumId);

		System.out.println(a);

		for (Map.Entry<String, List<FormDataBodyPart>> entry : map.entrySet()) {

			for (FormDataBodyPart part : entry.getValue()) {

				System.out.println(getExtension(part.getName()));
				Picture p = new Picture(a, getExtension(part.getName()));

				int pictureId = ServiceProvider.getPictureService().save(p);

				p = ServiceProvider.getPictureService().get(pictureId);

				String name = p.getCode() + "." + p.getType();

				System.out.println(p);

				try (OutputStream outPut = new FileOutputStream(
						new File("D:\\Documents\\school\\wac\\uploads\\" + a.getCode() + "\\" + name));
						InputStream fileContent = part.getEntityAs(InputStream.class)) {

					int read = 0;
					final byte[] bytes = new byte[1024];

					while ((read = fileContent.read(bytes)) != -1) {
						outPut.write(bytes, 0, read);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return Response.ok("cool upload").build();
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

	private String getExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}

		return extension;
	}
}
