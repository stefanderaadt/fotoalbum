package nl.hu.fotoalbum.webservices;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import nl.hu.fotoalbum.persistence.Album;
import nl.hu.fotoalbum.persistence.AlbumDAO;

@Path("/hello")
public class AppResource {
	
	private static SessionFactory factory; 

	@GET
	//@RolesAllowed("admin")
	@Produces(MediaType.TEXT_HTML)
	public String getPage() { 
		/*try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		Session session = factory.getCurrentSession();

        session.beginTransaction();
        Album a = session.get(Album.class, 3);
        System.out.println(a);
        System.out.println(a.getSharedUserIds());
        session.getTransaction().commit();
        
        factory.close();*/
		
		AlbumDAO albumDAO = new AlbumDAO();
		
		System.out.println(albumDAO.get(Album.class, 1));
        
		return "<h1>app</h1>";
	}
}
