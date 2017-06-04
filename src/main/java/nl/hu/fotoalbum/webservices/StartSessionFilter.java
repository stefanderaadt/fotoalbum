package nl.hu.fotoalbum.webservices;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import nl.hu.fotoalbum.persistence.BaseDAO;

@Provider
public class StartSessionFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext arg0) throws IOException {
		BaseDAO.startSession();
	}

}
