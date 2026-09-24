# orienteer-twilio

Sends SMS through the Twilio REST API.
- Data model: `OSMS`, `OPreparedSMS`, `OSmsSettings`.
- A hook sends a message when an `OPreparedSMS` is saved.
- A status callback resource receives delivery updates.

- **Module:** `org.orienteer.twilio.module.OTwilioModule`, module name `orienteer-twilio`, v2.
  - Guice: `org.orienteer.twilio.service.OTwilioInitModule`, which builds `ITwilioService` with Retrofit
    (base URL `https://api.twilio.com/`). Registered in `META-INF/services/com.google.inject.Module`.
  - Wicket: `org.orienteer.twilio.Initializer`.
- **Packages:** `hook`, `model`, `module`, `repository`, `resource`, `service`, `util`.
- **Key deps:**
  - Retrofit 2.7.2 with `converter-jackson` and `adapter-rxjava2`
  - RxJava 2.2.19 (EOL)
  - OkHttp 3 (transitive, EOL)
  - **Not** the Twilio SDK: the `twilio.version` 7.40.0 property is declared but unused
- **Tests:** 1 (`TwilioHttpServiceTest`), `@Ignore`d because it calls the live API.

## Pitfalls

- Never commit Twilio account SIDs or tokens.
- Not listed in root `modules.xml`.
- notification depends on this module.

## Upgrade risk: LOW–MEDIUM

No servlet coupling. Move to a current Retrofit (2.11+/3.x) and RxJava 3, or to plain `java.net.http`/OkHttp 4+.
Remove the unused property.
