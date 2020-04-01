package org.orienteer.core.dao;

import com.google.inject.ProvidedBy;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass("DAOTestClassB")
public interface IDAOTestClassB {
	public String getAlias();
	
	public IDAOTestClassA getLinkToA();
}
