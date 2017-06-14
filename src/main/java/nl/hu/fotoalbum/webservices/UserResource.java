package nl.hu.fotoalbum.webservices;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import nl.hu.fotoalbum.persistence.User;
import nl.hu.fotoalbum.services.ServiceProvider;

@Path("/user")
public class UserResource {
	final static public Key key = MacProvider.generateKey();
	
	@GET
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentUserInfo(@Context ContainerRequestContext requestCtx) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();
		
		User u = ServiceProvider.getUserService().getByEmail(email);
		
		return Response.ok(mapper.writeValueAsString(u)).build();
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("email") String email, @FormParam("password") String password) {
		try {
			password = hashSha256(password);
			
			User u = ServiceProvider.getUserService().login(email, password);
			if (u == null) {
				throw new IllegalArgumentException("No user found!");
			}

			// Issue a token for the user
			Calendar expiration = Calendar.getInstance();
			expiration.add(Calendar.MINUTE, 30);

			// Role standaard user
			String token = Jwts.builder().setSubject(email).claim("role", "user").setExpiration(expiration.getTime())
					.signWith(SignatureAlgorithm.HS512, key).compact();
			// Return the token on the response
			return Response.ok(token).build();
		} catch (JwtException | IllegalArgumentException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response register(@FormParam("firstname") String firstname, @FormParam("lastname") String lastname,
			@FormParam("email") String email, @FormParam("password") String password) {
		
		//Check if user doesn't exist allready
		if (ServiceProvider.getUserService().getByEmail(email) != null) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		
		password = hashSha256(password);
		
		User u = new User(firstname, lastname, email, password);
		
		int id = ServiceProvider.getUserService().save(u);
		
		return Response.ok("success").build();
	}
	
	private String hashSha256(String s){
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(s.getBytes("UTF-8"));
			s = DatatypeConverter.printHexBinary(hash);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return s;
	}
}
