package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.query.Query;

public class AlbumDAO extends BaseDAO {
	public Integer save(Album a) {
		transaction = session.beginTransaction();
		int id = (int) session.save(a);
		transaction.commit();

		return id;
	}

	public List<Album> getByCode(String code) {
		String q = "FROM Album WHERE code = :code";
		Query query = session.createQuery(q);
		query.setParameter("code", code);

		return query.list();
	}

	public List<Album> getPublic() {
		String q = "FROM Album WHERE share_type = 'P'";
		Query query = session.createQuery(q);

		return query.list();
	}
}
