package org.orienteer.twilio.service;

import io.reactivex.Completable;
import retrofit2.http.*;

import java.util.List;

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
}
