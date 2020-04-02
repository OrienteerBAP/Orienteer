package org.orienteer.notifications.testenv;

import com.google.inject.ProvidedBy;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.notifications.model.ONotification;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = OTestNotification.CLASS_NAME, superClasses = ONotification.CLASS_NAME)
public interface OTestNotification extends ONotification {

  String CLASS_NAME = "OTestNotification";

}
