package nl.hu.fotoalbum.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class BaseDAO {
	protected static Session session;
	protected static Transaction transaction;
	
	private static SessionFactory getSessionFactory(){
		SessionFactory sessionFactory = null;
		
		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		return sessionFactory;
	}
	
	public static void startSession(){
		session = getSessionFactory().openSession();
		//transaction = session.beginTransaction();
	}
	
	public static void stopSession(){
		//if(transaction.isActive()) transaction.commit();
		if(session.isOpen()) session.close();
	}
	
	//Default DAO functions
	public <T> T save(final T o) {
		
		int id = (Integer) session.save(o);
		
		System.out.println(id);
		
		return (T) session.get(o.getClass(), id);
	}

	public void delete(final Object object) {
		session.delete(object);
	}

	public <T> T get(final Class<T> type, final int id) {		
		Object o = session.get(type, id);
		
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
	
	//Getters
	public Session getSession(){
		return session;
	}
	
	public Transaction getTransaction(){
		return transaction;
	}
}
