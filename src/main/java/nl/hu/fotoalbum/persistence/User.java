package nl.hu.fotoalbum.persistence;

import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "useraccount")
public class User {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Integer id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	@JsonIgnore
	private String password;
	
	@OneToMany(mappedBy="user")
	@JsonIgnore
	private List<Album> albums;
	
	@ElementCollection
	@CollectionTable(name="hassharedusers", joinColumns=@JoinColumn(name="user_id"))
	@Column(name="album_id")
	@JsonIgnore
	public Set<Integer> sharedAlbumIds;

	public User() {
	}

	public User(String firstName, String lastName, String email, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	public Integer getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
    @JoinColumn(name="user_id")
	public List<Album> getAlbums(){
		return albums;
	}
    
    public void setAlbums(List<Album> albums){
    	this.albums = albums;
    }
    
	public Set<Integer> getSharedAlbumIds() {
		return sharedAlbumIds;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", albums=" + albums + "]";
	}
}
