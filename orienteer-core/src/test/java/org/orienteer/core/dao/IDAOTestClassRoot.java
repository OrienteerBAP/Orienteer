package org.orienteer.core.dao;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OClass;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = "DAOTestClassRoot", isAbstract= true)
public interface IDAOTestClassRoot {

	@DAOFieldIndex(type = OClass.INDEX_TYPE.NOTUNIQUE)
	public String getRoot();
}