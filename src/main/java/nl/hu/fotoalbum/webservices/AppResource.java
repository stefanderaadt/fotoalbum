package nl.hu.fotoalbum.webservices;

import java.util.Date;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import nl.hu.fotoalbum.persistence.Album;

@Path("/hello")
public class AppResource {
	
	private static SessionFactory factory; 

	@GET
	//@RolesAllowed("admin")
	@Produces(MediaType.TEXT_HTML)
	public String getPage() { 
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		Session session = factory.getCurrentSession();
		
		Album a = new Album("Title", "Desc", "P", new Date());

        session.beginTransaction();
        int id = (int) session.save(a);
        Album b = session.get(Album.class, id);
        session.getTransaction().commit();
        
        System.out.println(b);
	
		return "<h1>app</h1>";
	}
}
