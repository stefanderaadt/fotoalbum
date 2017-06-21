package nl.hu.fotoalbum.services;

import java.util.List;

import nl.hu.fotoalbum.persistence.User;
import nl.hu.fotoalbum.persistence.UserDAO;

public class UserService {
	UserDAO userDAO = new UserDAO();
	
	//Get user
	public User get(int id){
		return userDAO.get(User.class, id);
	}
	
	//Save user - Register
	public Integer save(User u){
		return userDAO.save(u);
	}
	
	//Get user by email
	public User getByEmail(String email){
		List <User> users = userDAO.getByEmail(email);
		
		if(users.isEmpty()) return null;
		
		return users.get(0);
	}
	
	//Login user
	public User login(String email, String password){
		return userDAO.login(email, password);
	}
	
	//Save or Update user
	public void saveOrUpdate(User u){
		userDAO.saveOrUpdate(u);
	}
}
