package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

public class UserDAO extends BaseDAO{
	public User login(String email, String password) {
		Criteria criteria = session.createCriteria(User.class);
		User u = (User) criteria.add(Restrictions.eq("email", email)).add(Restrictions.eq("password", password));
		
		return u;
	}
	
	public List<User> getByEmail(String email){	
		String q = "FROM User WHERE email = :email";
		Query query = session.createQuery(q);
		query.setParameter("email", email); 
		
		return query.list();
	}
}
