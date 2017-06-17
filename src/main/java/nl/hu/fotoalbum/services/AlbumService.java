package nl.hu.fotoalbum.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;
import nl.hu.fotoalbum.persistence.Picture;
import nl.hu.fotoalbum.persistence.User;

public class AlbumService {	
	AlbumDAO albumDAO = new AlbumDAO();
	
	public Integer save(Album a){
		//Save album and generate random code
		String code = generateCode(8);
		while(!albumDAO.getByCode(code).isEmpty()){
			code = generateCode(8);
		}
		a.setCode(code);
		
		return albumDAO.save(a);
	}
	
	public Album get(int id){
		return albumDAO.get(Album.class, id);
	}
	
	public Album getByCode(String code){
		List <Album> albums = albumDAO.getByCode(code);
		
		if(albums.isEmpty()) return null;
		
		return albums.get(0);
	}
	
	public void saveOrUpdate(Album a){
		albumDAO.saveOrUpdate(a);
	}
	
	public void delete(Album a){
		for(Picture p: a.getPictures()){
			ServiceProvider.getPictureService().delete(p);
		}

		albumDAO.delete(a);
	}
	
	public List<Album> getAll(){
		return albumDAO.getAll(Album.class);
	}
	
	public List<Album> getPublic(){
		return albumDAO.getPublic();
	}
	
	public List<Album> getSharedWithUser(User u){
		List<Album> albums =  new ArrayList<Album>();
		
		for(int id: u.getSharedAlbumIds()){
			albums.add(get(id));
		}
		
		return albums;
	}
	
	public List<Album> getFromUser(int id){
		return albumDAO.getFromUser(id);
	}
	
	public List<User> getSharedUsers(Album a){
		List<User> users =  new ArrayList<User>();
		
		for(int id : a.getSharedUserIds()){
			users.add(ServiceProvider.getUserService().get(id));
		}
		
		return users;
	}
	
	private String generateCode(int length){
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		
		return sb.toString();
	}
}
