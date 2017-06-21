package nl.hu.fotoalbum.webservices;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import nl.hu.fotoalbum.persistence.BaseDAO;

@Provider
@PreMatching
public class StartSessionFilter implements ContainerRequestFilter {

	//Filter to start hibernate session at start of request
	@Override
	public void filter(ContainerRequestContext arg0) throws IOException {
		//System.out.println("start");
		BaseDAO.startSession();
	}

}
