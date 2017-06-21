package nl.hu.fotoalbum.webservices;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import nl.hu.fotoalbum.persistence.BaseDAO;

@Provider
public class EndSessionFilter implements ContainerResponseFilter{

	//Filter to stop hibernate session at end of request
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		//System.out.println("stop");
		BaseDAO.stopSession();
	}

}
