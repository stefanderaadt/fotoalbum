package nl.hu.fotoalbum.webservices;

import java.security.Key;
import java.util.Calendar;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;
import nl.hu.fotoalbum.persistence.User;
import nl.hu.fotoalbum.persistence.UserDAO;
import nl.hu.fotoalbum.services.AlbumService;
import nl.hu.fotoalbum.services.ServiceProvider;

public class UserResource {
	final static public Key key = MacProvider.generateKey();

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password) {
		try {
			// Authenticate the user against the database
			String role = "";
			User u = ServiceProvider.getUserService().login(username, password);
			
			if (u != null) role = "user";

			if (role == null) {
				throw new IllegalArgumentException("No user found!");
			}

			// Issue a token for the user
			Calendar expiration = Calendar.getInstance();
			expiration.add(Calendar.MINUTE, 30);

			String token = Jwts.builder().setSubject(username).claim("role", role).setExpiration(expiration.getTime())
					.signWith(SignatureAlgorithm.HS512, key).compact();
			// Return the token on the response
			return Response.ok(token).build();
		} catch (JwtException | IllegalArgumentException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
	
	public Response register(){
		return null;
		
	}
}
