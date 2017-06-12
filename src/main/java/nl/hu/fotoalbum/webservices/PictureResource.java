package nl.hu.fotoalbum.webservices;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.Picture;
import nl.hu.fotoalbum.services.ServiceProvider;

@Path("/picture")
public class PictureResource {

	@GET
	@Path("/{picturecode}")
	public Response getPicture(@PathParam("picturecode") String code) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		Picture p = ServiceProvider.getPictureService().getByCode(code);

		return Response.ok(mapper.writeValueAsString(p)).build();
	}

	@POST
	@Path("{albumcode}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadPicture(@PathParam("albumcode") String albumCode, FormDataMultiPart multipart)
			throws JsonProcessingException {
		Map<String, List<FormDataBodyPart>> map = multipart.getFields();

		Album a = ServiceProvider.getAlbumService().getByCode(albumCode);
		Picture p = null;
		String path = "";

		for (Map.Entry<String, List<FormDataBodyPart>> entry : map.entrySet()) {

			for (FormDataBodyPart part : entry.getValue()) {

				try (InputStream fileContent = part.getEntityAs(InputStream.class)) {
					String extension = getExtension(part.getName());
					String id = getImgurContent(fileContent, extension);
					path = "http://i.imgur.com/" + id + "." + extension;
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p = new Picture(a, path);

				ServiceProvider.getPictureService().save(p);
			}
		}

		return Response.ok("uploaded").build();
	}

	// @RolesAllowed("user")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAlbum(@PathParam("picturecode") String code) throws JsonProcessingException {
		Picture p = ServiceProvider.getPictureService().getByCode(code);

		ServiceProvider.getPictureService().delete(p);

		return Response.ok("deleted").build();
	}

	// Get fileextension of image
	private String getExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}

		return extension;
	}

	// Upload image to imgur api and receive path
	private String getImgurContent(InputStream imageIs, String extension) throws Exception {
		URL url;

		url = new URL("https://api.imgur.com/3/image");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		BufferedImage image = ImageIO.read(imageIs);
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		ImageIO.write(image, "png", byteArray);
		byte[] byteImage = byteArray.toByteArray();
		String dataImage = new Base64().encodeAsString(byteImage);

		String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(dataImage, "UTF-8");

		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Client-ID a54132efd839ded"); // Client
																				// id
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		conn.connect();
		StringBuilder stb = new StringBuilder();
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		// Get the response
		InputStream is;
		if (((HttpURLConnection) conn).getResponseCode() == 400) {
			is = ((HttpURLConnection) conn).getErrorStream();
			System.out.println("error");
		} else {
			is = conn.getInputStream();
		}

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		String line;
		while ((line = rd.readLine()) != null) {
			stb.append(line).append("\n");
		}
		wr.close();
		rd.close();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode response = mapper.readTree(stb.toString());

		return response.get("data").get("id").asText();
	}
}
