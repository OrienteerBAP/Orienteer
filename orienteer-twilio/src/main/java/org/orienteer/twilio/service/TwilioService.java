package org.orienteer.twilio.service;

import com.google.common.base.Strings;
import com.google.inject.Singleton;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.twilio.model.OPreparedSMS;
import org.orienteer.twilio.model.OSmsSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Singleton
public class TwilioService implements ITwilioService {

  private static final Logger LOG = LoggerFactory.getLogger(TwilioService.class);

  @Override
  public void sendSMS(OPreparedSMS preparedSMS) throws Exception {
    OSmsSettings settings = preparedSMS.getSMS().getSettings();
    String from = settings.getTwilioPhoneNumber();

    if (!Strings.isNullOrEmpty(from)) {
      DBClosure.sudoConsumer(db -> preparedSMS.save());

      MessageCreator creator = Message.creator(
              new PhoneNumber(preparedSMS.getRecipient()),
              new PhoneNumber(from),
              preparedSMS.getText()
      );

      creator.setMediaUrl(getAttachmentUrls(preparedSMS));
      creator.create();
      preparedSMS.setSuccess(true);
      DBClosure.sudoConsumer(db -> preparedSMS.save());
    }
  }

  @Override
  public void sendSMSAsync(OPreparedSMS preparedSMS) {
    OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
    OrienteerWebSession session = OrienteerWebSession.get();
    RequestCycle cycle = RequestCycle.get();

    ForkJoinPool.commonPool()
            .execute(() -> {
              ThreadContext.setApplication(app);
              ThreadContext.setSession(session);
              ThreadContext.setRequestCycle(cycle);

              try {
                sendSMS(preparedSMS);
              } catch (Exception e) {
                LOG.error("Error during sending SMS: {}", preparedSMS, e);
              }

            });
  }

  private List<URI> getAttachmentUrls(OPreparedSMS sms) {
    return sms.getAttachments().stream()
            .map(URI::create)
            .collect(Collectors.toCollection(LinkedList::new));
  }
}
