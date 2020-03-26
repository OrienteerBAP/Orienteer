package org.orienteer.core.dao;

import java.util.List;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

@ProvidedBy(DAOProvider.class)
public interface ITestDAO {

	@Query("select from OPerspective")
	public List<ODocument> listOPerspective();
	
	
	@Query("select from OPerspective where alias = :alias")
	public ODocument findSingleAsDocument(String alias);
	
	@Query("select from OPerspective where alias = :alias")
	public IOPerspective findSingleAsDAO(String alias);
	
	@Query("select from OPerspective")
	public List<ODocument> findAllAsDocument();
	
	@Query("select from OPerspective")
	public List<IOPerspective> findAllAsDAO();
	
	default public int countPerspectives() {
		return listOPerspective().size();
	}
}
