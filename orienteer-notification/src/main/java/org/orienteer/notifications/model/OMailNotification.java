package org.orienteer.notifications.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.mail.model.OPreparedMail;

public class OMailNotification extends ONotification {

  public static final String CLASS_NAME = "OMailNotification";

  public static final String PROP_PREPARED_MAIL = "preparedMail";

  public OMailNotification() {
    this(CLASS_NAME);
  }

  public OMailNotification(String iClassName) {
    super(iClassName);
  }

  public OMailNotification(ODocument iDocument) {
    super(iDocument);
  }

  public OPreparedMail getPreparedMail() {
    ODocument mail = getPreparedMailAsDocument();
    return mail != null ? new OPreparedMail(mail) : null;
  }

  public ODocument getPreparedMailAsDocument() {
    OIdentifiable mail = document.field(PROP_PREPARED_MAIL);
    return mail != null ? mail.getRecord() : null;
  }

  public OMailNotification setPreparedMail(OPreparedMail mail) {
    return setPreparedMailAsDocument(mail != null ? mail.getDocument() : null);
  }

  public OMailNotification setPreparedMailAsDocument(ODocument mail) {
    document.field(PROP_PREPARED_MAIL, mail);
    return this;
  }
}
