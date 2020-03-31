package org.orienteer.core.dao;

import com.google.inject.ProvidedBy;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass("DAOTestClassA")
public interface IDAOTestClassA {

	public String getName();
	public void setName();
	
	public IDAOTestClassB getBSingle();
	
	@DAOField("bOtherField")
	public IDAOTestClassB getBOther();
	
	public IDAOTestClassA getSelfType();
}
