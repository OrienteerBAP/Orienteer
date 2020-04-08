package org.orienteer.core.dao;

import com.google.inject.ProvidedBy;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass("IDAODummyClass")
public interface IDAODummyClass {

	public String getName();
	public IDAODummyClass setName(String name);
}
