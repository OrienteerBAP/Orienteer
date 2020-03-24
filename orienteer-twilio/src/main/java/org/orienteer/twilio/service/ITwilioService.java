package org.orienteer.twilio.service;

import io.reactivex.Completable;
import okhttp3.Credentials;
import org.orienteer.twilio.model.OPreparedSMS;
import org.orienteer.twilio.model.OSMS;
import org.orienteer.twilio.model.OSmsSettings;
import retrofit2.http.*;

import java.util.List;

/**
 * HTTP service for work with Twilio API
 */
public interface ITwilioService {

  @FormUrlEncoded
  @POST("2010-04-01/Accounts/{accountSid}/Messages.json")
  Completable sendMessage(
          @Path("accountSid") String accountSid,
          @Field("To") String to,
          @Field("From") String from,
          @Field("Body") String text,
          @Field("StatusCallback") String callback,
          @Header("Authorization") String basicAuth
  );

  @FormUrlEncoded
  @POST("2010-04-01/Accounts/{accountSid}/Messages.json")
  Completable sendMessage(
          @Path("accountSid") String accountSid,
          @Field("To") String to,
          @Field("From") String from,
          @Field("Body") String text,
          @Header("Authorization") String basicAuth
  );

  @FormUrlEncoded
  @POST("2010-04-01/Accounts/{accountSid}/Messages.json")
  Completable sendMessage(
          @Path("accountSid") String accountSid,
          @Field("To") String to,
          @Field("From") String from,
          @Field("Body") String text,
          @Field("MediaUrl") List<String> mediaUrl,
          @Header("Authorization") String basicAuth
  );

  @FormUrlEncoded
  @POST("2010-04-01/Accounts/{accountSid}/Messages.json")
  Completable sendMessage(
          @Path("accountSid") String accountSid,
          @Field("To") String to,
          @Field("From") String from,
          @Field("Body") String text,
          @Field("MediaUrl") List<String> mediaUrl,
          @Field("StatusCallback") String callback,
          @Header("Authorization") String basicAuth
  );

  default Completable sendMessage(OPreparedSMS preparedSMS) {
    OSMS sms = preparedSMS.getSMS();
    OSmsSettings settings = sms.getSettings();
    String auth = Credentials.basic(settings.getTwilioAcountSid(), settings.getTwilioAuthToken());
    return sendMessage(settings.getTwilioAcountSid(), preparedSMS.getRecipient(), settings.getTwilioPhoneNumber(), preparedSMS.getText(), auth);
  }
}
