package org.orienteer.core.dao;

import org.orienteer.transponder.annotation.EntityType;

import com.google.inject.ProvidedBy;

@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType("DAOTestClassB")
public interface IDAOTestClassB {
	public String getAlias();
	
	public IDAOTestClassA getLinkToA();
	
	public IDAOTestParametrized<IDAOTestClassA> getParameterizedLink();
}
