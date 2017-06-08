package nl.hu.fotoalbum.services;

import java.io.File;
import java.util.Random;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.Picture;
import nl.hu.fotoalbum.persistence.PictureDAO;

public class PictureService {
	final private String uploadFolder = "C:/Users/Stefan/Documents/School/WAC/uploads/";
	
	PictureDAO pictureDAO = new PictureDAO();
	
	public Integer save(Picture p){
		//Save album and generate random code
		String code = generateCode(8);
		while(!pictureDAO.getByCode(code).isEmpty()){
			code = generateCode(8);
		}
		p.setCode(code);
		p.setId(getNextId(p.getAlbum()));
		return pictureDAO.save(p);
	}
	
	public Picture get(int id){
		return pictureDAO.get(Picture.class, id);
	}
	
	public void delete(Picture p){
		
		File file = new File(uploadFolder+p.getAlbum().getCode()+"/"+p.getCode()+"."+p.getType());
		
		if (file.delete()){
			pictureDAO.delete(p);
		}
	}
	
	public Integer getNextId(Album a) {
		return pictureDAO.getNextId(a);
	}
	
	public Picture getByCode(String code){
		return pictureDAO.getByCode(code).get(0);
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
