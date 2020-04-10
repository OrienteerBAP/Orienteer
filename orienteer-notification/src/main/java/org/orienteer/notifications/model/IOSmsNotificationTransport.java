package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.twilio.model.OSmsSettings;

/**
 * Wrapper class for {@link IOSmsNotificationTransport#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOSmsNotificationTransport.CLASS_NAME)
public interface IOSmsNotificationTransport extends IONotificationTransport {

  String CLASS_NAME = "OSmsNotificationTransport";

  @DAOField(linkedClass = OSmsSettings.CLASS_NAME, type = OType.LINK)
  ODocument getSmsSettings();
  IOSmsNotificationTransport setSmsSettings(ODocument smsSettings);

}
