package org.orienteer.twilio.util;

import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.MapModel;

import java.util.Map;

public final class OTwilioUtils {

  private OTwilioUtils() {}

  /**
   * Apply macros for given string
   * @param str original string
   * @param macros map of macroses
   * @return string with applied macros
   */
  public static String applyMacros(String str, Map<String, Object> macros) {
    return new StringResourceModel("", new MapModel<>(macros)).setDefaultValue(str).getString();
  }
}
