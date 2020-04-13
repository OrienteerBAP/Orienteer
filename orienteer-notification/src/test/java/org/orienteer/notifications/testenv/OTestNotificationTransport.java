package org.orienteer.notifications.testenv;

import com.google.inject.ProvidedBy;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.notifications.model.IONotificationTransport;


@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = OTestNotificationTransport.CLASS_NAME)
public interface OTestNotificationTransport extends IONotificationTransport {

  String CLASS_NAME = "OTestNotificationTransport";

}
