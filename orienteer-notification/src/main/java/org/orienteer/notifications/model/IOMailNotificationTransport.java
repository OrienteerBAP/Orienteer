package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.mail.model.OMailSettings;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.OrientDBProperty;

/**
 * Wrapper for class {@link IOMailNotificationTransport#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(IOMailNotificationTransport.CLASS_NAME)
public interface IOMailNotificationTransport extends IONotificationTransport {

  String CLASS_NAME = "OMailNotificationTransport";

  @EntityProperty(referencedType = OMailSettings.CLASS_NAME)
  @OrientDBProperty(type = OType.LINK)
  ODocument getMailSettings();
  IOMailNotificationTransport setMailSettings(ODocument mailSettings);

}
