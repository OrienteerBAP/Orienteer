package org.orienteer.core.dao;

import java.util.List;
import java.util.Map;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass("DAOTestClass")
public interface IDAOTestClass extends IODocumentWrapper {
	public String getName();
	public List<IDAOTestClass> getChild();
	public IDAOTestClass setChild(List<IDAOTestClass> childs);
	
	@DAOField("name")
	public String getNameSynonymMethod();
	
	public Map<String, IDAOTestClass> getLinkMap();
	public IDAOTestClass setLinkMap(Map<String, IDAOTestClass> map);
	
	@DAOField(value = "linkMap", linkedClass = "DAOTestClass")
	public Map<String, ODocument> getLinkMapAsDocuments();
	public IDAOTestClass setLinkMapAsDocuments(Map<String, ODocument> val);
	
	@DAOField(value = "child", linkedClass = "DAOTestClass")
	public List<ODocument> getChildAsDocuments();
	public IDAOTestClass setChildAsDocuments(List<ODocument> val);
	
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
