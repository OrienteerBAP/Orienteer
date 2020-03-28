package org.orienteer.core.dao;

import java.util.List;
import java.util.Map;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

@ProvidedBy(ODocumentWrapperProvider.class)
public interface IOPerspective extends IODocumentWrapper {
	public String getAlias();
	public Map<String, Object> getName();
	
	@DAOField("alias")
	public String getAliasSynonymMethod();
	
	default public String getTestAlias() {
		return "test"+getAlias();
	}
	
	default public String getTest2Alias() {
		return "test2"+getDocument().field("alias");
	}
	
	default public String getTest3Alias() {
		return "test3"+getTestAlias();
	}
	
	@Lookup("select from OPerspective where alias = :alias")
	public boolean lookupToBoolean(String alias);
	
	@Lookup("select from OPerspective where alias = :alias")
	public IOPerspective lookupAsChain(String alias);
	
	@Query("select expand(menu) from OPerspective where @rid = :target")
	public List<ODocument> listAllMenu();
	
	public List<ODocument> getMenu();
	
}
