package org.orienteer.notifications.testenv;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.notifications.model.ONotification;

public class OTestNotification extends ONotification {

  public static final String CLASS_NAME = "OTestNotification";

  public OTestNotification() {
    this(CLASS_NAME);
  }

  public OTestNotification(String iClassName) {
    super(iClassName);
  }

  public OTestNotification(ODocument iDocument) {
    super(iDocument);
  }

  @Override
  public String getDescription() {
    return CLASS_NAME;
  }
}
