package nl.hu.fotoalbum.persistence;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "picture")
public class Picture {
	@Id
	@Column(name = "id")
	@JsonIgnore
	private Integer id;

	@Column(name = "code")
	private String code;

	@Column(name = "type")
	private String type;

	@ManyToOne
	@JoinColumn(name = "album_id")
	@JsonIgnore
	private Album album;

	public Picture() {
	}

	public Picture(Album album, String type) {
		this.album = album;
		this.type = type;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Picture [album=" + album.getId() + " code=" + code + ", type=" + type + "]";
	}
}
