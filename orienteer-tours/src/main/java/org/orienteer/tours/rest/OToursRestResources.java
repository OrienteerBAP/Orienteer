package org.orienteer.tours.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.orienteer.tours.model.IOToursDAO;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.tours.model.IOTour;

/**
 * REST resource to work with tours 
 */
@Path("")
@Produces("application/json")
public class OToursRestResources {
	
	@GET
	@Path("tours")
	public List<IOTour> getAllowedTours() {
		return OrienteerWebApplication.get().getServiceInstance(IOToursDAO.class).listTours();
	}
}
