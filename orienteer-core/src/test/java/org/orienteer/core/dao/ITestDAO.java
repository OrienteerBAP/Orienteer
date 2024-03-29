package org.orienteer.core.dao;

import java.util.List;

import org.orienteer.transponder.annotation.Query;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

@ProvidedBy(DAOProvider.class)
public interface ITestDAO {

	@Query("select from DAOTestClass")
	public List<ODocument> listDAOTestClass();
	
	
	@Query("select from DAOTestClass where name = :name")
	public ODocument findSingleAsDocument(String name);
	
	@Query("select from DAOTestClass where name = :name")
	public IDAOTestClass findSingleAsDAO(String name);
	
	@Query("select from DAOTestClass")
	public List<ODocument> findAllAsDocument();
	
	@Query("select from DAOTestClass")
	public List<IDAOTestClass> findAllAsDAO();
	
	default public int countAll() {
		return listDAOTestClass().size();
	}
}
