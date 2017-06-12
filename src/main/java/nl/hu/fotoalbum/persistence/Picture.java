package nl.hu.fotoalbum.persistence;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "picture")
public class Picture implements Serializable{
	@Id
	@Column(name = "id")
	@JsonIgnore
	private Integer id;

	@Column(name = "code")
	private String code;
	
	@Column(name = "path")
	private String path;

	@ManyToOne
	@Id
	@JoinColumn(name = "album_id")
	@JsonIgnore
	private Album album;

	public Picture() {
	}

	public Picture(Album album, String path) {
		this.album = album;
		this.path = path;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "Picture [album=" + album.getId() + "id=" + id + " code=" + code + ", path=" + path + "]";
	}
}
