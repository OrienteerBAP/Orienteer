package org.orienteer.notifications.service;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.record.impl.ODocument;
import io.reactivex.Completable;
import okhttp3.Credentials;
import org.orienteer.core.dao.IODocumentWrapper;
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
import java.util.List;

/**
 * Transport for send notification throughout SMS
 */
public class OSmsTransport implements ITransport {

  private static final Logger LOG = LoggerFactory.getLogger(OSmsTransport.class);

  private final String accountSid;
  private final String authToken;
  private final String from;
  private final String callback;
  private final ITwilioService twilioService;

  public OSmsTransport(ODocument transportDocument) {
    OSmsNotificationTransport transport = IODocumentWrapper.get(OSmsNotificationTransport.class, transportDocument);
    OSmsSettings settings = new OSmsSettings(transport.getSmsSettings());

    accountSid = settings.getTwilioAcountSid();
    authToken = settings.getTwilioAuthToken();
    from = settings.getTwilioPhoneNumber();
    twilioService = OTwilioUtils.getService();
    callback = ONotificationModuleRepository.getModule().getSmsStatusUrl();
  }

  @Override
  public void send(ODocument notification) {
    OSmsNotification smsNotification = IODocumentWrapper.get(OSmsNotification.class, notification);
    LOG.info("Send notification: {}", notification);

    sendMessage(smsNotification).blockingAwait();
  }

  private Completable sendMessage(OSmsNotification notification) {
    OPreparedSMS preparedSms = new OPreparedSMS(notification.getPreparedSms());
    List<String> attachments = preparedSms.getAttachments();
    String auth = Credentials.basic(accountSid, authToken);

    if (Strings.isNullOrEmpty(callback)) {
      return twilioService.sendMessage(accountSid, preparedSms.getRecipient(), from, preparedSms.getText(), attachments, auth);
    }

    return twilioService.sendMessage(accountSid, preparedSms.getRecipient(), from, preparedSms.getText(), attachments, callback, auth);
  }

  @Override
  public void close() throws IOException {

  }
}
