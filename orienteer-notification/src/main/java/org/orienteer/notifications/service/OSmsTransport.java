package org.orienteer.notifications.service;

import com.google.common.base.Strings;
import io.reactivex.Completable;
import okhttp3.Credentials;
import org.orienteer.notifications.model.OSmsNotification;
import org.orienteer.notifications.model.OSmsNotificationTransport;
import org.orienteer.notifications.repository.ONotificationModuleRepository;
import org.orienteer.twilio.model.OPreparedSMS;
import org.orienteer.twilio.model.OSmsSettings;
import org.orienteer.twilio.service.ITwilioService;
import org.orienteer.twilio.util.OTwilioUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OSmsTransport implements ITransport<OSmsNotification> {

  private static final Logger LOG = LoggerFactory.getLogger(OSmsTransport.class);

  private final String accountSid;
  private final String authToken;
  private final String from;
  private final String callback;
  private final ITwilioService twilioService;

  public OSmsTransport(OSmsNotificationTransport transport) {
    OSmsSettings settings = transport.getSettings();

    accountSid = settings.getTwilioAcountSid();
    authToken = settings.getTwilioAuthToken();
    from = settings.getTwilioPhoneNumber();
    twilioService = OTwilioUtils.getService();
    callback = ONotificationModuleRepository.getModule().getSmsStatusUrl();
  }

  @Override
  public void send(OSmsNotification notification) {
    LOG.info("Send notification: {}", notification.getDescription());

    sendMessage(notification).blockingAwait();
  }

  private Completable sendMessage(OSmsNotification notification) {
    OPreparedSMS preparedSms = notification.getPreparedSms();
    String auth = Credentials.basic(accountSid, authToken);

    if (Strings.isNullOrEmpty(callback)) {
      return twilioService.sendMessage(accountSid, preparedSms.getRecipient(), from, preparedSms.getText(), auth);
    }

    return twilioService.sendMessage(accountSid, preparedSms.getRecipient(), from, preparedSms.getText(), callback, auth);
  }

  @Override
  public void close() throws IOException {

  }
}
