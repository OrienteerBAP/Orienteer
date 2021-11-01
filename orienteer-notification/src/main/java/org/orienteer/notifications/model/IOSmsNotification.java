package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.OrientDBProperty;
import org.orienteer.twilio.model.OPreparedSMS;

/**
 * Wrapper class for {@link IOSmsNotification#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(IOSmsNotification.CLASS_NAME)
public interface IOSmsNotification extends IONotification {

  String CLASS_NAME = "OSmsNotification";

  @EntityProperty(referencedType = OPreparedSMS.CLASS_NAME)
  @OrientDBProperty(type = OType.LINK)
  ODocument getPreparedSms();
  IONotification setPreparedSms(ODocument preparedSms);

}
