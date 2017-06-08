package nl.hu.fotoalbum.services;

import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.User;
import nl.hu.fotoalbum.persistence.UserDAO;

public class UserService {
	UserDAO userDAO = new UserDAO();
	
	public User get(int id){
		return userDAO.get(User.class, id);
	}
	
	public User getByEmail(String email){
		List<User> users = userDAO.getByEmail(email);
		
		if(users.size() > 1) return null;
		
		return userDAO.getByEmail(email).get(0);
	}
	
	public User login(String email, String password){
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			
			password = new String(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return userDAO.login(email, password);
	}
}
