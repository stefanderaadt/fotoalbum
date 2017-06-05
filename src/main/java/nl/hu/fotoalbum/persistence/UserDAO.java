package nl.hu.fotoalbum.persistence;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class UserDAO extends BaseDAO{
	public User login(String email, String password) {
		Criteria criteria = session.createCriteria(User.class);
		User u = (User) criteria.add(Restrictions.eq("email", email)).add(Restrictions.eq("password", password));
		
		return u;
	}
}
