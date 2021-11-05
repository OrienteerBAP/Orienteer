package org.orienteer.core.dao;

import org.orienteer.transponder.annotation.EntityType;

import com.google.inject.ProvidedBy;

@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType("DAOTestParametrized")
public interface IDAOTestParametrized<M> {

}
