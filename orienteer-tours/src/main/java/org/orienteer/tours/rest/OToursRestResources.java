package org.orienteer.tours.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.orienteer.core.OrienteerWebSession;
import static org.orienteer.core.util.CommonUtils.*;
import org.orienteer.tours.model.OTour;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * REST resource to work with tours 
 */
@Path("")
@Produces("application/json")
public class OToursRestResources {
	
	@GET
	@Path("tours")
	public List<OTour> getAllowedTours() {
		return mapIdentifiables(OrienteerWebSession.get().getDatabase().query(new OSQLSynchQuery<ODocument>("select from "+OTour.OCLASS_NAME)), 
										OTour::new);
		
	}
}
