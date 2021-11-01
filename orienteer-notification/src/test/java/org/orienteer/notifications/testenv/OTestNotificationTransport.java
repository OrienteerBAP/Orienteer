package org.orienteer.notifications.testenv;

import com.google.inject.ProvidedBy;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.notifications.model.IONotificationTransport;
import org.orienteer.transponder.annotation.EntityType;


@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(OTestNotificationTransport.CLASS_NAME)
public interface OTestNotificationTransport extends IONotificationTransport {

  String CLASS_NAME = "OTestNotificationTransport";

}
