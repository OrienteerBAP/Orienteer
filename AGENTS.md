# Orienteer

Orienteer is a Business Application Platform: an Apache Wicket web UI on top of OrientDB with a
dynamic, DB-driven data model, dashboards/widgets, a pluggable module system and REST.
The code was dormant from Feb 2024 to Sep 2026 and is being modernized.
**Read [`REFRESH_PLAN.md`](REFRESH_PLAN.md) before touching dependencies, build or infra, and tick items off there when done.**

## Stack (current → target)

| Area | Current | Target (see plan) |
|---|---|---|
| Java | `--release 21` (`maven.compiler.release`); builds and tests on JDK 21 and 25 | same |
| Web | Wicket 8.15, javax.servlet 3.x, Jetty 9.4.58 | Wicket 10.x, jakarta Servlet 6, Jetty 12 (ee10) |
| DI | Guice 6.0 + guice-servlet (javax.inject) | Guice 7 (jakarta.inject) |
| DB | OrientDB 3.2.56 + GraalJS 25.0.4 (D8), embedded by default; Hazelcast 3.12.13 when distributed | latest OrientDB 3.2.x |
| UI | CoreUI 3.4 / Bootstrap 4, Font Awesome 4.7, jQuery 3.4 (webjars) | CoreUI 5 / Bootstrap 5 |
| Tests | JUnit Platform: JUnit 4 tests (custom Guice runner) on the vintage engine, Jupiter 5.14 for new tests; Mockito 5 | same, migrate gradually |
| Own libs | wicket-orientdb 2.0-SNAPSHOT, transponder-orientdb 1.1-SNAPSHOT | released versions (maintained in their own repos) |

## Build status — read first

- Dependency resolution: the root pom uses Maven Central plus the **Central Portal snapshots** repository, where the
  external Orienteer libraries publish their snapshots (plan D9). Only `wicket-orientdb:2.0-SNAPSHOT` is published so far;
  `transponder-orientdb:1.1-SNAPSHOT`, `wicket-console:1.4-SNAPSHOT` and `logger:1.4-SNAPSHOT` resolve only after a local
  `./mvnw install` in their repos (~/Development/Transponder, wicket-console, orienteer-logger), until they're deployed.
- `./mvnw clean install` is green on JDK 21 and 25 (since P3): 104 tests run, 0 failures, 16 skipped (see plan Appendix C).
  Dev machine: Temurin 21 via SDKMAN (`.sdkmanrc`), Temurin 25 also installed; use `./mvnw` (Maven 3.9.16).
  Homebrew JDK 27 is non-LTS: don't target it. CI: `.github/workflows/ci.yml` (JDK 21/25, build only).
- **JVM flags** (property `orienteer.jvm.args` in the root pom; surefire uses it, `.mvn/jvm.config` has the same for
  `jetty:run`; also pass them to `java -jar` for standalone): `--add-opens java.base/java.lang=ALL-UNNAMED`
  (Wicket 8 cglib proxies) and `--enable-native-access=ALL-UNNAMED` (GraalJS). Keep all three places in sync.
- **Parked modules** (plan D3/P8): birt, bpm, camel, graph, taucharts and tours are only in the opt-in profile
  `parked` and are not built by default.
- `.travis.yml`, `PITCHME.*`, `Procfile`, `system.properties` are dead leftovers (removed in P9/P10).

## Commands (once dependencies resolve)

```bash
./mvnw clean install                                   # full build, tests, checkstyle (verify, failOnViolation)
./mvnw -DskipTests install                             # fast build
./mvnw -pl orienteer-core -am -DskipTests install      # one module + what it needs
./mvnw -pl orienteer-mail test -Dtest=TestOMailModule  # single test class
./mvnw -Pparked -pl orienteer-graph -am verify         # try a parked module
./mvnw -Ddocker-build package                          # profile `dockerbuild`: core + war only
cd orienteer-<module> && ../mvnw jetty:run             # run Orienteer + that module on :8080 (-Djetty.port=...), embedded OrientDB
```

## Layout

- `orienteer-core` — the platform; every module depends on it. Also publishes a **test-jar** (test infra).
- `orienteer-war` (deployable WAR / Docker payload), `orienteer-standalone` (embedded-Jetty uber-jar),
  `orienteer-archetype-jar|war` (Maven archetypes).
- Feature modules: architect, birt*, bpm*, camel*, devutils, etl, graph*, logger-server, mail, metrics,
  notification, pages, pivottable, rproxy, taucharts*, tours*, twilio, users (* = parked, profile `parked`).
- **Each module has its own `AGENTS.md`** (purpose, entry points, deps, pitfalls, upgrade risk) — read it first.
- `modules.xml` — catalog read at runtime by the dynamic loader (from the GitHub raw URL); out of sync.
- Root `orienteer.properties` / `orienteer-test.properties` — dev/test config (tracked despite `.gitignore`).
- `orienteer-core/config/` — OrientDB distributed, Hazelcast and backup configs.

## Architecture essentials

- Startup: `web.xml` → `OrienteerFilter` → `StartupPropertiesLoader` → module class loaders (`core.boot.loader`)
  → Guice (`OrienteerInitModule` → `OrienteerModule` + every `com.google.inject.Module` from ServiceLoader)
  → `GuiceFilter` → `OrienteerWebApplication` (extends wicket-orientdb `OrientDbWebApplication`).
- A module = subclass of `AbstractOrienteerModule` (name, version, dependencies; `onInstall/onUpdate/onInitialize/onDestroy`),
  persisted in OrientDB class `OModule`, registered by an `Initializer implements org.apache.wicket.IInitializer`
  listed in `src/main/resources/META-INF/services/org.apache.wicket.IInitializer`.
  Bump the module version to run `onUpdate` schema migrations.
- Guice extension/override: list a module in `META-INF/services/com.google.inject.Module`; `@OverrideModule` overrides bindings.
- Data: Transponder DAO interfaces (`core.dao.DAO`, `@EntityType`, `@OrienteerOClass/@OrienteerOProperty`),
  schema DSL `OSchemaHelper`, raw `ODocument` where needed.
- UI extension points: `@Widget`, `@MountPath`, `@OMethod`, `UIVisualizersRegistry`.
  Wicket markup (`.html`) sits next to its Java class in `src/main/java`.
- Config precedence: `orienteer-default.properties` < `<qualifier>.properties` (default `orienteer`; looked up via
  system property, cwd upwards, `~/.orienteer`, `$ORIENTEER_HOME`) < env vars (`_`→`.`) < system properties.

## Conventions

- Java 21 source level: modern Java (`var`, records, text blocks, switch expressions) is fine in new code; don't rewrite old code just to modernize it.
- Indentation is mostly tabs but mixed — match the surrounding file; no reformat-only diffs.
- Interfaces **must** start with `I` (Checkstyle). OrientDB-related types use `O` (`OClass…`, DAO ifaces `IO…`).
  Schema-name constants: `CLASS_NAME`, `PROP_*` / `OPROPERTY_*`.
- Checkstyle (`check_style.xml`) fails the build on: missing Javadoc on public types, missing `package-info.java`,
  interface naming, line length > 200, uncommented `main`.
- Logging: SLF4J `private static final Logger LOG = LoggerFactory.getLogger(X.class);`.
- i18n: `<Name>.properties` + `<Name>_ru.utf8.properties` + `<Name>_uk.utf8.properties`; add keys to all three.
- Lombok is available (provided scope) but used sparingly — don't spread it.
- Maven: **all plugin and third-party versions live in the root pom** (`pluginManagement` / `dependencyManagement`, properties);
  module poms declare no versions. Parked modules keep their own until P8. `./mvnw -Pconvergence validate` reports conflicts.

## Testing

- JUnit 4 style, `@RunWith(OrienteerTestRunner.class)` from the core test-jar (`org.orienteer.junit`), run on the JUnit
  Platform (vintage engine; Jupiter is available for plain unit tests). The runner boots a full
  embedded Orienteer + OrientDB once per JVM using `orienteer-test.properties` → `TestEnvOrienteerWebApplication`.
  Tests are integration tests and share state (test classes are Guice `@Singleton`s).
- `@Inject OrienteerTester tester;` for WicketTester; `@Sudo` to run as admin.
- Surefire excludes `**/*Slow*` (network-dependent tests); `-Pfulltest` runs everything.
- `@Ignore` only with a reason in the annotation and a plan item (the 5 loader tests that need a dead snapshot repo are P4).

## Pitfalls

- Don't bump one library in isolation: Wicket/Guice/servlet/Jetty/jakarta move together (plan P5–P6), and
  Hazelcast is pinned to 3.12.x by OrientDB distributed.
- Running or testing creates `runtime/`, `databases/`, `Orienteer/`, `libs/` in the working dir — never commit them.
- GraalVM 21.3.5 from `orientdb-core` is excluded in the root pom (D8). If a new OrientDB artifact pulls `org.graalvm*` 21.x back in, exclude it there too.
- `@ProvidedBy(DAOProvider/ODocumentWrapperProvider)` needs `AbstractDynamicProvider.bindProvisionListener()` in the injector (done in `OModulesInitModule` and `OrienteerModule`).
- `log4j2.xml` ships inside core and every module jar; loader defaults in `orienteer-default.properties` use dead/HTTP repo URLs.
- No real credentials in committed files; the tracked properties hold dev defaults only.
