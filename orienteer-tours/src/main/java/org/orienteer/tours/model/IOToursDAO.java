package org.orienteer.tours.model;

import java.util.List;

import org.orienteer.core.dao.DAOProvider;
import org.orienteer.transponder.annotation.Query;

import com.google.inject.ProvidedBy;

/**
 * DAO to access OTours data 
 */
@ProvidedBy(DAOProvider.class)
public interface IOToursDAO {

	@Query("select from "+IOTour.OCLASS_NAME)
	public List<IOTour> listTours();
}
