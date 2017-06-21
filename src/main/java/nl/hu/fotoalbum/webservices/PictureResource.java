package nl.hu.fotoalbum.webservices;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.Picture;
import nl.hu.fotoalbum.services.ServiceProvider;

@Path("/picture")
public class PictureResource {
	private ObjectMapper mapper = new ObjectMapper();

	//Get picture by code
	@GET
	@Path("{picturecode}")
	@RolesAllowed("user")
	public Response getPicture(@PathParam("picturecode") String code, @Context ContainerRequestContext requestCtx)
			throws JsonProcessingException {
		// Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();
		
		//Get picture by code
		Picture p = ServiceProvider.getPictureService().getByCode(code);
		
		//Check if picture is found or return 404
		if(p == null) return Response.status(Response.Status.NOT_FOUND).build();

		//Build json string for picture
		return Response.ok(pictureToJson(p, email)).build();
	}

	//Upload picture
	@POST
	@Path("{albumcode}")
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadPicture(@PathParam("albumcode") String albumCode, FormDataMultiPart multipart,
			@Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		//Get album by code
		Album a = ServiceProvider.getAlbumService().getByCode(albumCode);
		
		//Check if album exists or return 404
		if(a == null) return Response.status(Response.Status.NOT_FOUND).build();

		// Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();

		//Check if album is from user
		if (!a.getUser().getEmail().equals(email))
			return Response.status(Response.Status.UNAUTHORIZED).build();

		//Get uploaded pictures
		Map<String, List<FormDataBodyPart>> map = multipart.getFields();
		Picture p = null;
		String path = "";

		//Loop through pictures
		for (Map.Entry<String, List<FormDataBodyPart>> entry : map.entrySet()) {

			//Get picture
			for (FormDataBodyPart part : entry.getValue()) {

				//Upload picture to imgur
				try (InputStream fileContent = part.getEntityAs(InputStream.class)) {
					String extension = getExtension(part.getName());
					String id = getImgurContent(fileContent, extension);
					path = "https://i.imgur.com/" + id + "." + extension.toLowerCase();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				//Create new picture object
				p = new Picture(a, path);

				//Save picture object
				ServiceProvider.getPictureService().save(p);
			}
		}

		//Create json response
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("response", "uploaded");

		//Return json response
		return Response.ok(mapper.writeValueAsString(responseNode)).build();
	}

	@DELETE
	@RolesAllowed("user")
	@Path("{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAlbum(@PathParam("code") String code, @Context ContainerRequestContext requestCtx) throws JsonProcessingException {
		//Get picture by code
		Picture p = ServiceProvider.getPictureService().getByCode(code);
		
		//Check if picture exists
		if(p == null) return Response.status(Response.Status.NOT_FOUND).build();
		
		// Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();
		
		//Check if user is owner of album
		if (!p.getAlbum().getUser().getEmail().equals(email)) return Response.status(Response.Status.UNAUTHORIZED).build();

		//Delete picture
		ServiceProvider.getPictureService().delete(p);
		
		//Create json response
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("response", "deleted");
		
		//Return json response
		return Response.ok(mapper.writeValueAsString(responseNode)).build();
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
		conn.setRequestProperty("Authorization", "Client-ID a54132efd839ded"); // Client id
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

	//Picture to json function
	private String pictureToJson(Picture p, String email) {
		//Create new ObjectNode
		ObjectNode pictureNode = mapper.convertValue(p, ObjectNode.class);

		//Add album to picture
		pictureNode.putPOJO("album", p.getAlbum());

		//Add isfromuser boolean
		pictureNode.put("isFromUser", p.getAlbum().getUser().getEmail().equals(email));

		try {
			//Try to return json string
			return mapper.writeValueAsString(pictureNode);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		//Return empty string if something went wrong
		return "";
	}
}
