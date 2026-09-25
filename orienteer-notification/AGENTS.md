# orienteer-notification

Generic notification framework:
- `IONotification` with transports and a status history
- mail transport (`OMailTransport`) and SMS transport (`OSmsTransport`) via twilio, pooled in `OTransportPool`
- a scheduler and a send task
- an SMS status callback resource

- **Module:** `org.orienteer.notifications.module.ONotificationModule`, module name `orienteer-notification`, v2.
  Depends on the mail and twilio modules. Registered by `org.orienteer.notifications.Initializer`.
  - Note the plural Java package (`notifications`) vs the singular artifactId.
- **Packages:** `hook`, `model`, `module`, `repository`, `resource`, `scheduler`, `service`, `task`.
- **Key deps:** only orienteer-core, orienteer-mail and orienteer-twilio.
  It also uses `javax.mail`, and okhttp3 only transitively.
- **Tests:** 12 files but only 5 `@Test`, 4 of them `@Ignore`d.
  - Only `TestNotificationLifecycle` runs.
  - Has a sizeable `testenv/` with test transports and factories, and a test `IInitializer`.

## Pitfalls

- Not listed in root `modules.xml`, so the dynamic loader UI doesn't offer it (P10).
- Declare the transitive okhttp usage explicitly, or replace it (P4, together with twilio's HTTP client).

## Upgrade risk: LOW–MEDIUM

Follows mail's jakarta rename and twilio's HTTP client upgrade. Ideally re-enable the ignored tests with fakes.
