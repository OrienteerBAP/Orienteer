package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.twilio.model.OPreparedSMS;

/**
 * Wrapper class for {@link OSmsNotification#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = OSmsNotification.CLASS_NAME, superClasses = ONotification.CLASS_NAME)
public interface OSmsNotification extends ONotification {

  String CLASS_NAME = "OSmsNotification";

  @DAOField(linkedClass = OPreparedSMS.CLASS_NAME, type = OType.LINK)
  ODocument getPreparedSms();

  @DAOField(linkedClass = OPreparedSMS.CLASS_NAME, type = OType.LINK)
  ONotification setPreparedSms(ODocument preparedSms);

}
