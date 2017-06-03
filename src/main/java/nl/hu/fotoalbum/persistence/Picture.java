package nl.hu.fotoalbum.persistence;

import javax.persistence.*;

@Entity
@Table(name = "picture")
public class Picture {
	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "path")
	private String path;
	
	@ManyToOne
    @JoinColumn(name="album_id")
    private Album album;

	public Picture() {
	}

	public Picture(Album album, Integer id, String path) {
		this.album = album;
		this.id = id;
		this.path = path;
	}
	
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
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

	@Override
	public String toString() {
		return "Picture [id=" + id + ", path=" + path + "]";
	}
}
