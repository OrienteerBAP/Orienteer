package org.orienteer.core.dao.dm;

import java.util.Map;

import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOFieldIndex;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.Lookup;
import org.orienteer.core.dao.ODocumentWrapperProvider;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;

/**
 * DAO interface for classes which represents enumerations
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOEnum.CLASS_NAME, isAbstract = true)
public interface IOEnum {
	
	public static final String CLASS_NAME = "OEnum";

	@DAOField(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
	public Map<String, String> getName();
	public IOEnum setName(Map<String, String> value);
	
	@DAOFieldIndex(type = INDEX_TYPE.UNIQUE)
	public String getAlias();
	public IOEnum setAlias(String value);
	
	@Lookup("select from :daoClass where alias = :alias")
	public boolean lookupByAlias(String alias);
}
