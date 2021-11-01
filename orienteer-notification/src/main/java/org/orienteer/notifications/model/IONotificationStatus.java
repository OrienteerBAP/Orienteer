package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.IODocumentWrapper;

import java.util.Map;

/**
 * Wrapper class for {@link IONotificationStatus#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(IONotificationStatus.CLASS_NAME)
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
