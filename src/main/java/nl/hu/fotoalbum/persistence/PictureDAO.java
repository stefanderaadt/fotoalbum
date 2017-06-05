package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

public class PictureDAO extends BaseDAO{
	public List<Picture> getByCode(String code){
		String q = "FROM Picture WHERE code = :code";
		Query query = session.createQuery(q);
		query.setParameter("code", code); 
		
		return query.list();
	}
}
