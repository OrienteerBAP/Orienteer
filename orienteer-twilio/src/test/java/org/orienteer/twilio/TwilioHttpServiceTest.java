package org.orienteer.twilio;

import com.google.inject.Inject;
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
