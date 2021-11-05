package org.orienteer.core.dao;

import org.orienteer.transponder.annotation.EntityType;

import com.google.inject.ProvidedBy;

@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType("IDAODummyClass")
public interface IDAODummyClass {

	public String getName();
	public IDAODummyClass setName(String name);
}
