package org.orienteer.notifications.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.notifications.service.ITransport;

import java.util.Collections;
import java.util.Map;

/**
 * Notification transport model
 */
public abstract class ONotificationTransport extends ODocumentWrapper {

  public static final String CLASS_NAME = "ONotificationTransport";

  public static final String PROP_NAME       = "name";
  public static final String PROP_ALIAS      = "alias";

  protected ONotificationTransport(String iClassName) {
    super(iClassName);
  }

  protected ONotificationTransport(ODocument iDocument) {
    super(iDocument);
  }

  public abstract ITransport<? extends ONotification> createTransportService();

  public Map<String, String> getName() {
    Map<String, String> name = document.field(PROP_NAME);
    return name != null ? name : Collections.emptyMap();
  }

  public ONotificationTransport setName(Map<String, String> name) {
    document.field(PROP_NAME, name);
    return this;
  }

  public String getAlias() {
    return document.field(PROP_ALIAS);
  }

  public ONotificationTransport setAlias(String alias) {
    document.field(PROP_ALIAS, alias);
    return this;
  }

}
