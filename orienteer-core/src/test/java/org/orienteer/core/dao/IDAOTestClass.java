package org.orienteer.core.dao;

import java.util.List;
import java.util.Map;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

@ProvidedBy(ODocumentWrapperProvider.class)
public interface IDAOTestClass extends IODocumentWrapper {
	public String getName();
	public List<IDAOTestClass> getChild();
	
	@DAOField("name")
	public String getNameSynonymMethod();
	
	default public String getTestName() {
		return "test"+getName();
	}
	
	default public String getTest2Name() {
		return "test2"+getDocument().field("name");
	}
	
	default public String getTest3Name() {
		return "test3"+getTestName();
	}
	
	@Lookup("select from DAOTestClass where name = :name")
	public boolean lookupToBoolean(String name);
	
	@Lookup("select from DAOTestClass where name = :name")
	public IDAOTestClass lookupAsChain(String name);
	
	@Query("select expand(child) from DAOTestClass where @rid = :target")
	public List<ODocument> listAllChild();
	
}
