package org.orienteer.core.dao.dm;

import java.util.Map;

import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.OrienteerOProperty;
import org.orienteer.transponder.annotation.EntityIndex;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityPropertyIndex;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.annotation.Lookup;
import org.orienteer.transponder.orientdb.ODriver;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;

/**
 * DAO interface for classes which represents enumerations
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = IOEnum.CLASS_NAME, isAbstract = true)
public interface IOEnum {
	
	public static final String CLASS_NAME = "OEnum";

	@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
	public Map<String, String> getName();
	public IOEnum setName(Map<String, String> value);
	
	@EntityPropertyIndex(type = ODriver.OINDEX_UNIQUE)
	public String getAlias();
	public IOEnum setAlias(String value);
	
	@Lookup("select from :daoClass where alias = :alias")
	public boolean lookupByAlias(String alias);
}
