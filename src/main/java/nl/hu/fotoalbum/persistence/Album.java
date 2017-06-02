package nl.hu.fotoalbum.persistence;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "album")
public class Album {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "share_type")
	private String shareType;
	
	@Column(name = "created_at", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	public Album(){}
	
	public Album(String title, String description, String shareType, Date createdAt) {
		this.title = title;
		this.description = description;
		this.shareType = shareType;
		this.createdAt = createdAt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "Album [id=" + id + ", title=" + title + ", description=" + description + ", shareType=" + shareType
				+ ", createdAt=" + createdAt + "]";
	}
}
