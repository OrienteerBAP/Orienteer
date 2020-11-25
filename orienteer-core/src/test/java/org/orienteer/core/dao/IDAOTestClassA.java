package org.orienteer.core.dao;

import java.util.List;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass("DAOTestClassA")
public interface IDAOTestClassA extends IDAOTestClassRoot{


	public String getName();
	public void setName(String name);
	
	public IDAOTestClassB getBSingle();
	
	@DAOField("bOtherField")
	public IDAOTestClassB getBOther();
	
	
	public List<String> getEmbeddedStringList();
	
	@DAOField(linkedClass = "DAOTestClassB")
	public ODocument getLinkAsDoc();
	
	@DAOField(linkedClass = "DAOTestClassB")
	public List<ODocument> getLinkList();
	public IDAOTestClassA getSelfType();
}
