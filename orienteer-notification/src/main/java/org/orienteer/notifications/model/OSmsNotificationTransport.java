package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.twilio.model.OSmsSettings;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = OSmsNotificationTransport.CLASS_NAME, superClasses = ONotificationTransport.CLASS_NAME)
public interface OSmsNotificationTransport extends ONotificationTransport {

  String CLASS_NAME = "OSmsNotificationTransport";

  @DAOField(linkedClass = OSmsSettings.CLASS_NAME, type = OType.LINK)
  ODocument getSmsSettings();

  @DAOField(linkedClass = OSmsSettings.CLASS_NAME, type = OType.LINK)
  OSmsNotificationTransport setSmsSettings(ODocument smsSettings);

}
