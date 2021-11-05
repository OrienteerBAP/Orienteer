package org.orienteer.core.dao;

import org.orienteer.transponder.annotation.EntityPropertyIndex;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.ODriver;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OClass;

@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = "DAOTestClassRoot", isAbstract= true)
public interface IDAOTestClassRoot {

	@EntityPropertyIndex(type = ODriver.OINDEX_NOTUNIQUE)
	public String getRoot();
}