package nl.hu.fotoalbum.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class BaseDAO {
	protected static SessionFactory sessionFactory;
	protected static Session currentSession;
	protected static Transaction currentTransaction;
	
	public BaseDAO(){
		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static SessionFactory getSessionFactory(){
		return sessionFactory;
	}
	
	public Session getCurrentSession() {
	    currentSession = getSessionFactory().openSession();
	    return currentSession;
	}
	
	public Session getCurrentSessionwithTransaction() {
	    currentSession = getSessionFactory().openSession();
	    currentTransaction = currentSession.beginTransaction();
	    return currentSession;
	}
	 
	public void closeCurrentSession() {
	    currentSession.close();
	}
	 
	public void closeCurrentSessionwithTransaction() {
	    currentTransaction.commit();
	    currentSession.close();
	}
}
