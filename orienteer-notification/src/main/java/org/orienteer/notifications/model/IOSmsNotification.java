package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.twilio.model.OPreparedSMS;

/**
 * Wrapper class for {@link IOSmsNotification#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOSmsNotification.CLASS_NAME)
public interface IOSmsNotification extends IONotification {

  String CLASS_NAME = "OSmsNotification";

  @DAOField(linkedClass = OPreparedSMS.CLASS_NAME, type = OType.LINK)
  ODocument getPreparedSms();
  IONotification setPreparedSms(ODocument preparedSms);

}
