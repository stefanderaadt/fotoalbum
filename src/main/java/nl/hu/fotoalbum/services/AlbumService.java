package nl.hu.fotoalbum.services;

import java.util.Random;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;

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
	
	public void saveOrUpdate(Album a){
		albumDAO.saveOrUpdate(a);
	}
	
	public void delete(Album a){
		albumDAO.delete(a);
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
