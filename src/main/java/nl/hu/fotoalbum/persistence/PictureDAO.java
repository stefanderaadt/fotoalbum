package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

public class PictureDAO extends BaseDAO {
	//Save picture
	public Integer save(Picture p){
		transaction = session.beginTransaction();
		Picture s = (Picture) session.save(p);
		transaction.commit();
		
		return s.getId();
	}
	
	//Get picture by code
	public List<Picture> getByCode(String code) {
		String q = "FROM Picture WHERE code = :code";
		Query query = session.createQuery(q);
		query.setParameter("code", code);

		return query.list();
	}
	
	//Get next id for picture
	public Integer getNextId(Picture p) {
	    String q = "FROM Picture WHERE album_id = :albumId";
	    Query query = session.createQuery(q);
	    query.setParameter("albumId", p.getAlbum().getId());

	    List<Picture> pictures =  query.list();

	    int id = 1;

	    if (pictures.size() > 0) {
	        id = pictures.get(pictures.size()-1).getId();
	        id++;
	    }

	    return id;
	}
}
