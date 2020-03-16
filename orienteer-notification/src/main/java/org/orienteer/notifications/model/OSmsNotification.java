package org.orienteer.notifications.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.twilio.model.OPreparedSMS;

/**
 * SMS notification
 */
public class OSmsNotification extends ONotification {

  public static final String CLASS_NAME = "OSmsNotification";

  public static final String PROP_PREPARED_SMS = "preparedSms";

  public OSmsNotification() {
    this(CLASS_NAME);
  }

  public OSmsNotification(String iClassName) {
    super(iClassName);
  }

  public OSmsNotification(ODocument iDocument) {
    super(iDocument);
  }

  @Override
  public String getDescription() {
    OPreparedSMS sms = getPreparedSms();
    return CLASS_NAME + "( from = " + sms.getSMS().getSettings().getTwilioPhoneNumber()
            + ", to = " + sms.getRecipient() + ", rid = " + getDocument().getIdentity().toString() + " )";
  }

  public OPreparedSMS getPreparedSms() {
    ODocument sms = getPreparedSmsAsDocument();
    return sms != null ? new OPreparedSMS(sms) : null;
  }

  public ODocument getPreparedSmsAsDocument() {
    OIdentifiable sms = document.field(PROP_PREPARED_SMS);
    return sms != null ? sms.getRecord() : null;
  }

  public OSmsNotification setPreparedSms(OPreparedSMS sms) {
    return setPreparedSms(sms != null ? sms.getDocument() : null);
  }

  public OSmsNotification setPreparedSms(ODocument sms) {
    document.field(PROP_PREPARED_SMS, sms);
    return this;
  }
}
