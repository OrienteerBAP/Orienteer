package org.orienteer.notifications.testenv;

import com.google.inject.ProvidedBy;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.notifications.model.IONotification;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = OTestNotification.CLASS_NAME)
public interface OTestNotification extends IONotification {

  String CLASS_NAME = "OTestNotification";

}
