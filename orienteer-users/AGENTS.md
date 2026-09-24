# orienteer-users

Multi-user support for Orienteer:
- self-registration and password restore by email
- the `orienteerUser` role and its permissions
- OAuth2 social login (GitHub, Facebook, Google)

- **Module:** `org.orienteer.users.module.OrienteerUsersModule`, module name `orienteer-users`, **v14**.
  Depends on the perspectives and mail modules.
  - Guice: `OrienteerUsersInitModule`, registered in `META-INF/services/com.google.inject.Module`.
  - Wicket: `org.orienteer.users.Initializer`.
- **Packages:** `component(.event|.panel|.visualizer)`, `hook`, `method`, `model`, `module`, `repository`, `resource`,
  `service(.impl)`, `util`, `validation`, `web`, `widget`.
- **Key deps:** `scribejava-apis`/`scribejava-core` 6.5.1 (2019), `jackson-databind` (root-managed), orienteer-mail, orienteer-devutils.
- **Tests:** 7 (1 `@Ignore`d), all WicketTester integration tests:
  `RegistrationComponentTest`, `RestorePasswordTest`, `RestorePasswordComponentTest`, `TestFunctionCall`.
  Plus a test Guice override.

## Pitfalls

- Schema changes need a module version bump (currently 14), with migration logic in `onUpdate`.
- OAuth client IDs and secrets are stored in the DB. Never put real ones in code or test fixtures.

## Upgrade risk: MEDIUM

- `ModalWindow` in `method/LinkToSocialNetwork.java` (removed in Wicket 10).
- `wicket.util.time.Time` in `RestorePasswordResource` (removed in Wicket 10).
- `javax.inject.Named` in the Guice module (Guice 7 is jakarta-only).
- scribejava 6 → 8 API changes.
- See P5, P6, P8.
