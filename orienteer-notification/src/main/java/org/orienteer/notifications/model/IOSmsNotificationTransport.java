package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.OrientDBProperty;
import org.orienteer.twilio.model.OSmsSettings;

/**
 * Wrapper class for {@link IOSmsNotificationTransport#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(IOSmsNotificationTransport.CLASS_NAME)
public interface IOSmsNotificationTransport extends IONotificationTransport {

  String CLASS_NAME = "OSmsNotificationTransport";

  @EntityProperty(referencedType = OSmsSettings.CLASS_NAME)
  @OrientDBProperty(type = OType.LINK)
  ODocument getSmsSettings();
  IOSmsNotificationTransport setSmsSettings(ODocument smsSettings);

}
