package org.orienteer.twilio.util;

import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.MapModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.twilio.service.ITwilioService;

import java.util.Map;

/**
 * Util class
 */
public final class OTwilioUtils {

  private OTwilioUtils() {}

  /**
   * Apply macros for given string
   * @param str original string
   * @param macros map of macroses
   * @return string with applied macros
   */
  public static String applyMacros(String str, Map<String, Object> macros) {
    if (macros == null) {
      return str;
    }
    return new StringResourceModel("", new MapModel<>(macros)).setDefaultValue(str).getString();
  }

  public static ITwilioService getService() {
    return OrienteerWebApplication.lookupApplication().getServiceInstance(ITwilioService.class);
  }
}
