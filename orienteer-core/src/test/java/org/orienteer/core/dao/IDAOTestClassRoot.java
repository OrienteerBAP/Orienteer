package org.orienteer.core.dao;

import com.google.inject.ProvidedBy;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = "DAOTestClassRoot", isAbstract= true)
public interface IDAOTestClassRoot {

	public String getRoot();
}