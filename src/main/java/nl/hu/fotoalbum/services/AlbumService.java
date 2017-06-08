package nl.hu.fotoalbum.services;

import java.io.File;
import java.util.List;
import java.util.Random;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;
import nl.hu.fotoalbum.persistence.Picture;

public class AlbumService {
	final private String uploadFolder = "C:/Users/Stefan/Documents/School/WAC/uploads/";
	
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
		return albumDAO.getByCode(code).get(0);
	}
	
	public void saveOrUpdate(Album a){
		albumDAO.saveOrUpdate(a);
	}
	
	public void delete(Album a){
		for(Picture p: a.getPictures()){
			ServiceProvider.getPictureService().delete(p);
		}
		
		File folder = new File(uploadFolder+a.getCode());
		
		if(folder.delete()){
			albumDAO.delete(a);
		}
	}
	
	public List<Album> getPublic(){
		return albumDAO.getPublic();
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
