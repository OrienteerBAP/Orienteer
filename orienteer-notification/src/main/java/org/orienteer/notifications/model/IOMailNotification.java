package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.OrientDBProperty;

/**
 * Wrapper for {@link IOMailNotification#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(IOMailNotification.CLASS_NAME)
public interface IOMailNotification extends IONotification {

  String CLASS_NAME = "OMailNotification";

  @EntityProperty(referencedType = OPreparedMail.CLASS_NAME)
  @OrientDBProperty(type = OType.LINK)
  ODocument getPreparedMail();
  IOMailNotification setPreparedMail(ODocument preparedMail);

}
