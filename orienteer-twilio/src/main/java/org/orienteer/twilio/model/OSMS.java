package org.orienteer.twilio.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * SMS template
 */
public class OSMS extends ODocumentWrapper {

  public static final String CLASS_NAME = "OSMS";

  public static final String PROP_NAME = "name";
  public static final String PROP_TEXT = "text";
  public static final String PROP_SETTINGS = "settings";

  public OSMS() {
    this(CLASS_NAME);
  }

  public OSMS(String iClassName) {
    super(iClassName);
  }

  public OSMS(ODocument iDocument) {
    super(iDocument);
  }

  public String getName() {
    return document.field(PROP_NAME);
  }

  public OSMS setName(String name) {
    document.field(PROP_NAME, name);
    return this;
  }

  public String getText() {
    return document.field(PROP_TEXT);
  }

  public OSMS setText(String text) {
    document.field(PROP_TEXT, text);
    return this;
  }

  public OSmsSettings getSettings() {
    ODocument settings = getSettingsAsDocument();
    return settings != null ? new OSmsSettings(settings) : null;
  }

  public ODocument getSettingsAsDocument() {
    OIdentifiable settings = document.field(PROP_SETTINGS);
    return settings != null ? settings.getRecord() : null;
  }

  public OSMS setSettings(OSmsSettings settings) {
    return setSettingsAsDocument(settings != null ? settings.getDocument() : null);
  }

  public OSMS setSettingsAsDocument(ODocument settings) {
    document.field(PROP_SETTINGS, settings);
    return this;
  }
}
