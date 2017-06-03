package nl.hu.fotoalbum.persistence;

public interface UserDAOInterface {
	public boolean create(User user);
	public User getById(int id);
	public boolean update(User user);
	public boolean delete(User user);
}
