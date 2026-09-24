# orienteer-mail

Sends email from Orienteer. `OMailSettings` holds the SMTP config, `OMail` is a template,
`OPreparedMail` is a concrete message, and `OMailAttachment` holds attachments.
`IOMailService` sends mail synchronously or asynchronously; there's also a send-mail task.

- **Module:** `org.orienteer.mail.OMailModule`, module name `orienteer-mail`, v7. Registered by `org.orienteer.mail.Initializer`.
- **Packages:** `model` (Transponder DAO interfaces), `service`, `task` (`IOSendMailTask`), `util`.
- **Key deps:** `javax.mail:mail:1.4.7` (2013, abandoned coordinates). 16 `javax.mail`/`javax.activation` imports.
- **Tests:** `TestOMailModule` (5, 3 of them `@Ignore`d) and `TestSendMail` (1).
  `TestInitModule` (Guice `@OverrideModule`, registered via test `META-INF/services`) swaps the mail service.
  `OMailServiceTest` is a stub.
- **Dependents:** logger-server, users and notification.

## Pitfalls

- Never commit real SMTP credentials into test data or properties.
- Tests that actually send mail are `@Ignore`d on purpose.

## Upgrade risk: LOW–MEDIUM

- Move to `jakarta.mail-api` 2.1 plus Eclipse Angus Mail, renaming `javax.mail`/`javax.activation` to their `jakarta.*` equivalents.
- This is independent of servlets, so it can be done any time. Scheduled with P6 so dependents move together.
