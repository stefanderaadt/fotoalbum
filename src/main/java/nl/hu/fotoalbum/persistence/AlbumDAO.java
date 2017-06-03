package nl.hu.fotoalbum.persistence;

import org.hibernate.Session;

public class AlbumDAO extends BaseDAO {
	
	private Session currentSession;
	
	 public boolean save(Album album){
		 currentSession = super.getSessionFactory().openSession();
		 return true;
	 }
}
