package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.query.Query;

public class AlbumDAO extends BaseDAO {
	//Save album
	public Integer save(Album a) {
		transaction = session.beginTransaction();
		int id = (int) session.save(a);
		transaction.commit();

		return id;
	}

	//Get album by code
	public List<Album> getByCode(String code) {
		String q = "FROM Album WHERE code = :code";
		Query query = session.createQuery(q);
		query.setParameter("code", code);

		return query.list();
	}

	//Get all public albums
	public List<Album> getPublic() {
		String q = "FROM Album WHERE share_type = 'P' ORDER BY created_at DESC";
		Query query = session.createQuery(q);

		return query.list();
	}
	
	//Get albums from user
	public List<Album> getFromUser(int id) {
		String q = "FROM Album WHERE user_id = :id ORDER BY created_at DESC";
		Query query = session.createQuery(q);
		query.setParameter("id", id);

		return query.list();
	}
}
