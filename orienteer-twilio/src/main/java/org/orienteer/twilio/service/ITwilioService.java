package org.orienteer.twilio.service;

import com.google.inject.ImplementedBy;
import org.orienteer.twilio.model.OPreparedSMS;

@ImplementedBy(TwilioService.class)
public interface ITwilioService {

  void sendSMS(OPreparedSMS sms) throws Exception;
  void sendSMSAsync(OPreparedSMS sms);

}
