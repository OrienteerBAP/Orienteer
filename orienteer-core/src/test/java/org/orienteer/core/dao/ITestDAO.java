package org.orienteer.core.dao;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;

@ProvidedBy(DAOProvider.class)
public interface ITestDAO {

	@Query("select from OPerspective")
	List<ODocument> listOPerspective();
	
	default int countPerspectives() {
		return listOPerspective().size();
	}
}
