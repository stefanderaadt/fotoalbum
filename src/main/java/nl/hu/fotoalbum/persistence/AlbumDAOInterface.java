package nl.hu.fotoalbum.persistence;

public interface AlbumDAOInterface {
	public boolean create(Album album);
	public Album getById(int id);
	public boolean update(Album album);
	public boolean delete(Album album);
}
