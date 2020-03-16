package org.orienteer.twilio;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import okhttp3.Credentials;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.twilio.service.ITwilioService;

import java.util.Collections;

@RunWith(OrienteerTestRunner.class)
public class TwilioHttpServiceTest {

  @Inject
  @Named("service.twilio")
  private ITwilioService twilioHttpService;


  @Test
  @Ignore
  public void testSendMessage() throws InterruptedException {
    String body = "Hello, World!";
    String from = "";
    String to = "";
    String accountSid = "";
    String authToken = "";

    twilioHttpService.sendMessage(accountSid, to, from, body, Collections.emptyList(), Credentials.basic(accountSid, authToken)).blockingAwait();
  }

}
