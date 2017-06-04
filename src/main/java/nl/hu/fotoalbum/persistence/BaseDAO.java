package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class BaseDAO {
	private static SessionFactory sessionFactory;
	private static Session session;

	public BaseDAO() {
		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public Session startSession(){
		session = sessionFactory.openSession();
		return session;
	}
	
	public void stopSession(){
		if(session.isOpen()) session.close();
	}

	public <T> T save(final T o) {
		return (T) session.save(o);
	}

	public void delete(final Object object) {
		session.delete(object);
	}

	public <T> T get(final Class<T> type, final int id) {
		Transaction tx = session.beginTransaction();
		
		Object o = session.get(type, id);
		
		tx.commit();
		
		return (T) o;
	}

	public <T> T merge(final T o) {
		return (T) session.merge(o);
	}

	public <T> void saveOrUpdate(final T o) {
		session.saveOrUpdate(o);
	}

	public <T> List<T> getAll(final Class<T> type) {
		final Criteria crit = session.createCriteria(type);
		return crit.list();
	}
}
