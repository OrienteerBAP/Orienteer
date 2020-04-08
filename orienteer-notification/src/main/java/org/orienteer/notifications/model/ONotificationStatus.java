package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;

import java.util.Map;

/**
 * Wrapper class for {@link ONotificationStatus#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = ONotificationStatus.CLASS_NAME)
public interface ONotificationStatus extends IODocumentWrapper {

  String CLASS_NAME = "ONotificationStatus";

  String ALIAS_PENDING = "pending";
  String ALIAS_SENDING = "sending";
  String ALIAS_SENT    = "sent";
  String ALIAS_FAILED  = "failed";


  String getAlias();
  ONotificationStatus setAlias(String alias);

  Map<String, String> getName();
  ONotificationStatus setName(Map<String, String> name);


}
