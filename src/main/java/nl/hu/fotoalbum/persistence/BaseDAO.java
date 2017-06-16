package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class BaseDAO {
	protected static SessionFactory sessionFactory;
	protected static Session session;
	protected static Transaction transaction;
	
	private static void startSessionFactory(){
		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	private static void stopSessionFactory(){
		sessionFactory.close();
	}
	
	public static void startSession(){
		startSessionFactory();
		session = sessionFactory.openSession();
	}
	
	public static void stopSession(){
		if(session.isOpen()) session.close();
		stopSessionFactory();
	}

	public void delete(final Object object) {
		transaction = session.beginTransaction();
		session.delete(object);
		transaction.commit();
	}

	public <T> T get(final Class<T> type, final int id) {		
		Object o = session.get(type, id);
		
		return (T) o;
	}

	public <T> T merge(final T o) {
		return (T) session.merge(o);
	}

	public <T> void saveOrUpdate(final T o) {
		transaction = session.beginTransaction();
		session.saveOrUpdate(o);
		transaction.commit();
	}

	public <T> List<T> getAll(final Class<T> type) {
		final Criteria crit = session.createCriteria(type);
		return crit.list();
	}
	
	//Getters
	public Session getSession(){
		return session;
	}
	
	public Transaction getTransaction(){
		return transaction;
	}
}
