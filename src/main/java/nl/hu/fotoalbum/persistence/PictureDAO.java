package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

public class PictureDAO extends BaseDAO {
	public List<Picture> getByCode(String code) {
		String q = "FROM Picture WHERE code = :code";
		Query query = session.createQuery(q);
		query.setParameter("code", code);

		return query.list();
	}

	public Integer getNextId(Album a) {
		String q = "FROM Picture WHERE album_id = :albumId";
		Query query = session.createQuery(q);
		query.setParameter("albumId", a.getId());

		List<Picture> pictures =  query.list();

		int id = 1;
		
		if (pictures.size() > 0) {
			id = pictures.get(pictures.size()-1).getId();
			id++;
		}

		return id;
	}
}
