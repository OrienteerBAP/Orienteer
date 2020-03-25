package org.orienteer.core.dao;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.Map;

@ProvidedBy(ODocumentWrapperProvider.class)
public interface IOPerspective extends IODocumentWrapper {
  String getAlias();

  Map<String, Object> getName();

  default String getTestAlias() {
    return "test" + getAlias();
  }

  default String getTest2Alias() {
    return "test2" + getDocument().field("alias");
  }

  default String getTest3Alias() {
    return "test3" + getTestAlias();
  }

  @Lookup("select from OPerspective where alias = :alias")
	void lookup(String alias);

  @Query("select expand(menu) from OPerspective where @rid = :target")
	List<ODocument> listAllMenu();

  List<ODocument> getMenu();
}
