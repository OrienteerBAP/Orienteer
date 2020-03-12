package org.orienteer.twilio.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

public class OSmsSettings extends ODocumentWrapper {

  public static final String CLASS_NAME = "OSmsSettings";

  public static final String PROP_NAME                = "name";
  public static final String PROP_TWILIO_ACCOUNT_SID  = "twilioAccountSid";
  public static final String PROP_TWILIO_AUTH_TOKEN   = "twilioAuthToken";
  public static final String PROP_TWILIO_PHONE_NUMBER = "twilioPhoneNumber";

  public OSmsSettings() {
    this(CLASS_NAME);
  }

  public OSmsSettings(String iClassName) {
    super(iClassName);
  }

  public OSmsSettings(ODocument iDocument) {
    super(iDocument);
  }

  public String getName() {
    return document.field(PROP_NAME);
  }

  public OSmsSettings setName(String name) {
    document.field(PROP_NAME, name);
    return this;
  }

  public String getTwilioAcountSid() {
    return document.field(PROP_TWILIO_ACCOUNT_SID);
  }

  public OSmsSettings setTwilioAcountSid(String twilioAcountSid) {
    document.field(PROP_TWILIO_ACCOUNT_SID, twilioAcountSid);
    return this;
  }

  public String getTwilioAuthToken() {
    return document.field(PROP_TWILIO_AUTH_TOKEN);
  }

  public OSmsSettings setTwilioAuthToken(String twilioAuthToken) {
    document.field(PROP_TWILIO_AUTH_TOKEN, twilioAuthToken);
    return this;
  }

  public String getTwilioPhoneNumber() {
    return document.field(PROP_TWILIO_PHONE_NUMBER);
  }

  public OSmsSettings setTwilioPhoneNumber(String twilioPhoneNumber) {
    document.field(PROP_TWILIO_PHONE_NUMBER, twilioPhoneNumber);
    return this;
  }
}
