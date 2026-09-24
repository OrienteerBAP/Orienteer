# orienteer-logger-server

Server side of the Orienteer incident logger.
- Receives events over HTTP at `/resource/ologger` (`OLoggerReceiverResource`) and stores them in OrientDB.
- Dispatches them with the default, filtered or mail dispatcher.
- Generates correlation IDs.

- **Module:** `org.orienteer.logger.server.OLoggerModule`, module name `orienteer-logger`, **v13**. Depends on the mail module.
  Registered by `org.orienteer.logger.server.Initializer`.
- **Packages:** `hook`, `model` (Transponder DAO interfaces), `resource`, `service(.correlation|.dispatcher|.enhancer)`, `util`.
- **Key deps:**
  - `org.orienteer:logger:1.4-SNAPSHOT`. **External and unresolvable**; release 1.3 is on Central.
  - orienteer-mail, orienteer-devutils
  - `javax.servlet-api` at compile scope
- **Tests:** 7 across `TestModule`, `TestOrienteerCorrelationIdGenerator`, `TestOLoggerEventFilteredDispatcher`
  and `TestOLoggerEventMailDispatcher`.
  - `TestInitModule` (`@OverrideModule`) swaps in `OTestLoggerMailService`.

## Pitfalls

- The receiver resource accepts unauthenticated GET/POST by design. Keep this in mind for security work (P10).
- Bump the module version when changing the schema.

## Upgrade risk: MEDIUM

- The jakarta changes are mechanical: servlet in `OLoggerReceiverResource`, mail through orienteer-mail.
- The blocker is the `logger` snapshot (External dependencies table in `REFRESH_PLAN.md`).
