package org.orienteer.notifications.model;

import com.google.common.base.Strings;
import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.notifications.service.ITransport;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Wrapper class for {@link IONotificationTransport#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IONotificationTransport.CLASS_NAME, isAbstract = true)
public interface IONotificationTransport extends IODocumentWrapper {

  String CLASS_NAME = "ONotificationTransport";


  String getAlias();
  IONotificationTransport setAlias(String alias);

  Map<String, String> getName();
  IONotificationTransport setName(Map<String, String> name);

  String getTransportClass();
  IONotificationTransport setTransportClass(String transportClass);

  default ITransport createTransportService() {
    String transportClass = getTransportClass();
    if (!Strings.isNullOrEmpty(transportClass)) {
      try {
        Constructor<?> constructor = Class.forName(transportClass).getConstructor(ODocument.class);
        return (ITransport) constructor.newInstance(getDocument());
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
    return null;
  }

}
