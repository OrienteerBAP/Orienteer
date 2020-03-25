package org.orienteer.core.dao;

import java.util.List;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

@ProvidedBy(DAOProvider.class)
public interface ITestDAO {

	@Query("select from OPerspective")
	public List<ODocument> listOPerspective();
	
	default public int countPerspectives() {
		return listOPerspective().size();
	}
}
