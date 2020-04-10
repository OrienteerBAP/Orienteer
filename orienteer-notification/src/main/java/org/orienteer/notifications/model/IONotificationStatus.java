package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;

import java.util.Map;

/**
 * Wrapper class for {@link IONotificationStatus#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IONotificationStatus.CLASS_NAME)
public interface IONotificationStatus extends IODocumentWrapper {

  String CLASS_NAME = "ONotificationStatus";

  String ALIAS_PENDING = "pending";
  String ALIAS_SENDING = "sending";
  String ALIAS_SENT    = "sent";
  String ALIAS_FAILED  = "failed";


  String getAlias();
  IONotificationStatus setAlias(String alias);

  Map<String, String> getName();
  IONotificationStatus setName(Map<String, String> name);


}
