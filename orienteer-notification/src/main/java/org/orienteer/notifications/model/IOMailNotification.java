package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.mail.model.OPreparedMail;

/**
 * Wrapper for {@link IOMailNotification#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOMailNotification.CLASS_NAME)
public interface IOMailNotification extends IONotification {

  String CLASS_NAME = "OMailNotification";

  @DAOField(linkedClass = OPreparedMail.CLASS_NAME, type = OType.LINK)
  ODocument getPreparedMail();
  IOMailNotification setPreparedMail(ODocument preparedMail);

}
