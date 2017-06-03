package nl.hu.fotoalbum.persistence;

public interface PictureDAOInterface {
	public boolean create(Picture picture);
	public Picture getById(int id);
	public boolean update(Picture picture);
	public boolean delete(Picture picture);
}
