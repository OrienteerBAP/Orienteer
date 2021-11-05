package org.orienteer.core.dao;

import java.util.List;

import org.orienteer.transponder.annotation.EntityIndex;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.ODriver;
import org.orienteer.transponder.orientdb.OrientDBProperty;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType("DAOTestClassA")
@EntityIndex(name="rootname", type=ODriver.OINDEX_NOTUNIQUE, properties = {"name", "root"})
public interface IDAOTestClassA extends IDAOTestClassRoot{


	public String getName();
	public void setName(String name);
	
	public IDAOTestClassB getBSingle();
	
	@EntityProperty("bOtherField")
	public IDAOTestClassB getBOther();
	
	
	public List<String> getEmbeddedStringList();
	
	@EntityProperty(referencedType = "DAOTestClassB")
	public ODocument getLinkAsDoc();
	
	@EntityProperty(referencedType = "DAOTestClassB")
	public List<ODocument> getLinkList();
	public IDAOTestClassA getSelfType();
}
