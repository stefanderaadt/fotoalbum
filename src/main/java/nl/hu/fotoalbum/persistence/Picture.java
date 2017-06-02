package nl.hu.fotoalbum.persistence;

import javax.persistence.*;

public class Picture {
	@Id
	@Column(name = "album_id")
	private Integer albumId;

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "path")
	private String path;

	public Picture() {
	}

	public Picture(Integer albumId, Integer id, String path) {
		this.albumId = albumId;
		this.id = id;
		this.path = path;
	}

	public Integer getAlbumId() {
		return albumId;
	}

	public void setAlbumId(Integer albumId) {
		this.albumId = albumId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
