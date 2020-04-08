package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.mail.model.OMailSettings;

/**
 * Wrapper for class {@link OMailNotificationTransport#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = OMailNotificationTransport.CLASS_NAME, superClasses = ONotificationTransport.CLASS_NAME)
public interface OMailNotificationTransport extends ONotificationTransport {

  String CLASS_NAME = "OMailNotificationTransport";

  @DAOField(linkedClass = OMailSettings.CLASS_NAME, type = OType.LINK)
  ODocument getMailSettings();
  OMailNotificationTransport setMailSettings(ODocument mailSettings);

}
