package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

public class UserDAO extends BaseDAO{
	//Save user - Register
	public Integer save(User u) {
		transaction = session.beginTransaction();
		int id = (int) session.save(u);
		transaction.commit();

		return id;
	}
	
	//Get user by email
	public List<User> getByEmail(String email){	
		String q = "FROM User WHERE email = :email";
		Query query = session.createQuery(q);
		query.setParameter("email", email); 
		
		return query.getResultList();
	}
	
	//Login user
	public User login(String email, String password) {
		String q = "FROM User WHERE email = :email AND password = :password";
		Query query = session.createQuery(q);
		query.setParameter("email", email);
		query.setParameter("password", password);

		User u = (User) query.uniqueResult();

		return u;
	}
}
