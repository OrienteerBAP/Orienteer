package org.orienteer.twilio.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class OTwilioInitModule extends AbstractModule {

  @Override
  protected void configure() {
    super.configure();
  }

  @Named("service.twilio")
  @Provides
  @Singleton
  public ITwilioService provideTwilioHttpService(@Named("twilio.url") String twilioUrl) {
    return new Retrofit.Builder()
            .baseUrl(twilioUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
            .create(ITwilioService.class);
  }

  @Named("twilio.url")
  @Provides
  public String provideTwilioApiUrl() {
    return "https://api.twilio.com/";
  }

}
