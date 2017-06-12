package nl.hu.fotoalbum.services;

import java.util.List;

import nl.hu.fotoalbum.persistence.User;
import nl.hu.fotoalbum.persistence.UserDAO;

public class UserService {
	UserDAO userDAO = new UserDAO();
	
	public User get(int id){
		return userDAO.get(User.class, id);
	}
	
	public Integer save(User u){
		return userDAO.save(u);
	}
	
	public User getByEmail(String email){
		List <User> users = userDAO.getByEmail(email);
		
		if(users.isEmpty()) return null;
		
		return users.get(0);
	}
	
	public User login(String email, String password){
		return userDAO.login(email, password);
	}
	
	public void saveOrUpdate(User u){
		userDAO.saveOrUpdate(u);
	}
}
