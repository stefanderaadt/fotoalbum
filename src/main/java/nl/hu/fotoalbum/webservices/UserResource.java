package nl.hu.fotoalbum.webservices;

import java.io.UnsupportedEncodingException;
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

import org.jsoup.Jsoup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import nl.hu.fotoalbum.persistence.User;
import nl.hu.fotoalbum.services.ServiceProvider;

@Path("/user")
public class UserResource {
	final static public Key key = MacProvider.generateKey();
	
	private ObjectMapper mapper = new ObjectMapper();

	//Get logged in user
	@GET
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentUserInfo(@Context ContainerRequestContext requestCtx) throws JsonProcessingException{		
		//Get users email/username from securitycontext
		String email = requestCtx.getSecurityContext().getUserPrincipal().getName();
		
		//Get user by email
		User u = ServiceProvider.getUserService().getByEmail(email);
		
		//Return user as json string
		return Response.ok(mapper.writeValueAsString(u)).build();
	}

	//User login
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("email") String email, @FormParam("password") String password) {
		//Try to get user
		try {
			//Hash password with sha256
			password = hashSha256(password);
			
			//Get user with email and password
			User u = ServiceProvider.getUserService().login(email, password);
			
			//Check if user is found
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

	//Register new user
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response register(@FormParam("firstname") String firstname, @FormParam("lastname") String lastname,
			@FormParam("email") String email, @FormParam("password") String password) throws JsonProcessingException {
		
		//Check if user doesn't exist
		if (ServiceProvider.getUserService().getByEmail(email) != null) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		
		//Remove html tags from string
		firstname = Jsoup.parse(firstname).text();
		lastname = Jsoup.parse(lastname).text();
		email = Jsoup.parse(email).text();
		
		//Hash password with sha256
		password = hashSha256(password);
		
		//Create new user object
		User u = new User(firstname, lastname, email, password);
		
		//Save user
		ServiceProvider.getUserService().save(u);
		
		//Create json response
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("response", "success");
		
		//Return json response
		return Response.ok(mapper.writeValueAsString(responseNode)).build();
	}
	
	//Hash sha256 function
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
