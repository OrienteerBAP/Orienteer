package org.orienteer.notifications.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import java.util.Collections;
import java.util.Map;

/**
 * Notification status
 */
public class ONotificationStatus extends ODocumentWrapper {

  public static final String CLASS_NAME = "ONotificationStatus";
  public static final String PROP_ALIAS = "alias";
  public static final String PROP_NAME  = "name";

  public static final String ALIAS_PENDING = "pending";
  public static final String ALIAS_SENDING = "sending";
  public static final String ALIAS_SENT    = "sent";
  public static final String ALIAS_FAILED  = "failed";

  public ONotificationStatus() {
    this(CLASS_NAME);
  }

  public ONotificationStatus(String iClassName) {
    super(iClassName);
  }

  public ONotificationStatus(ODocument iDocument) {
    super(iDocument);
  }

  public String getAlias() {
    return document.field(PROP_ALIAS);
  }

  public ONotificationStatus setAlias(String alias) {
    document.field(PROP_ALIAS, alias);
    return this;
  }

  public Map<String, String> getName() {
    Map<String, String> name = document.field(PROP_NAME);
    return name != null ? name : Collections.emptyMap();
  }

  public ONotificationStatus setName(Map<String, String> name) {
    document.field(PROP_NAME, name);
    return this;
  }
}
