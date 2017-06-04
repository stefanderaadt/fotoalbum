package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class BaseDAO {
	private static SessionFactory sessionFactory;

	public BaseDAO() {
		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public <T> T save(final T o) {
		return (T) sessionFactory.getCurrentSession().save(o);
	}

	public void delete(final Object object) {
		sessionFactory.getCurrentSession().delete(object);
	}

	public <T> T get(final Class<T> type, final int id) {
		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		
		Object o = sessionFactory.getCurrentSession().get(type, id);
		
		tx.commit();
		
		return (T) o;
	}

	public <T> T merge(final T o) {
		return (T) sessionFactory.getCurrentSession().merge(o);
	}

	public <T> void saveOrUpdate(final T o) {
		sessionFactory.getCurrentSession().saveOrUpdate(o);
	}

	public <T> List<T> getAll(final Class<T> type) {
		final Session session = sessionFactory.getCurrentSession();
		final Criteria crit = session.createCriteria(type);
		return crit.list();
	}
}
