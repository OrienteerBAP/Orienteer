package org.orienteer.notifications.testenv;

import com.google.inject.ProvidedBy;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.notifications.model.IONotification;
import org.orienteer.transponder.annotation.EntityType;

@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(OTestNotification.CLASS_NAME)
public interface OTestNotification extends IONotification {

  String CLASS_NAME = "OTestNotification";

}
