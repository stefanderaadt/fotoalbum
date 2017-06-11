package nl.hu.fotoalbum.persistence;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "album")
public class Album {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	//@JsonIgnore
	private Integer id;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;
	
	@Column(name = "share_type")
	private String shareType;
	
	@Column(name = "created_at", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@OneToMany(mappedBy="album")
	@JsonBackReference
	private List<Picture> pictures;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	@JsonIgnore
	private User user;
	
	@ElementCollection
	@CollectionTable(name="hassharedusers", joinColumns=@JoinColumn(name="album_id"))
	@Column(name="user_id")
	public Set<Integer> sharedUserIds;
	
	public Album(){}
	
	public Album(String title, String description, String shareType, User user) {
		this.title = title;
		this.description = description;
		this.shareType = shareType;
		this.user = user;
		this.createdAt = new Date();
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
	
    @JoinColumn(name="album_id")
	public List<Picture> getPictures(){
		return pictures;
	}

	public void setPictures(List<Picture> pictures){
		this.pictures = pictures;
	}
    
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<Integer> getSharedUserIds() {
		//Return null if sharetype isn't U
		if (!"U".equals(shareType)) return null;
		
		return sharedUserIds;
	}

	public void setSharedUserIds(Set<Integer> sharedUserIds) {
		this.sharedUserIds = sharedUserIds;
	}

	@Override
	public String toString() {
		return "Album [id=" + id + ", code=" + code + ", title=" + title + ", description=" + description
				+ ", shareType=" + shareType + ", createdAt=" + createdAt
				+ ", pictures=" + pictures + "]";
	}
}
