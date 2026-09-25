# Orienteer

Orienteer is a Business Application Platform: an Apache Wicket web UI on top of OrientDB with a
dynamic, DB-driven data model, dashboards/widgets, a pluggable module system and REST.
The code was dormant from Feb 2024 to Sep 2026 and is being modernized.
**Read [`REFRESH_PLAN.md`](REFRESH_PLAN.md) before touching dependencies, build or infra, and tick items off there when done.**

## Stack (current → target)

| Area | Current | Target (see plan) |
|---|---|---|
| Java | 1.8 (`java.version` in root pom) | `--release 21`, CI also on JDK 25 |
| Web | Wicket 8.15, javax.servlet 3.x, Jetty 9.4.12 | Wicket 10.x, jakarta Servlet 6, Jetty 12 (ee10) |
| DI | Guice 4.2 + guice-servlet | Guice 7 (jakarta.inject) |
| DB | OrientDB 3.2.27, embedded by default; Hazelcast 3.x when distributed | latest OrientDB 3.2.x (pins Hazelcast 3.12) |
| UI | CoreUI 3.4 / Bootstrap 4, Font Awesome 4.7, jQuery 3.4 (webjars) | CoreUI 5 / Bootstrap 5 |
| Tests | JUnit 4 + custom Guice runner, Mockito 2 | JUnit Jupiter (vintage bridge first), Mockito 5 |
| Own libs | wicket-orientdb 2.0-SNAPSHOT, transponder-orientdb 1.1-SNAPSHOT | released versions (maintained in their own repos) |

## Build status — read first

- Dependency resolution: the root pom uses Maven Central plus the **Central Portal snapshots** repository, where the
  external Orienteer libraries publish their snapshots (plan D9). `wicket-orientdb:2.0-SNAPSHOT` is published there.
  Still **unresolvable**: `transponder-orientdb:1.1-SNAPSHOT` (core, so everything), `wicket-console:1.4-SNAPSHOT`
  (devutils) and `logger:1.4-SNAPSHOT` (logger-server). They come from their own repos; this repo doesn't own them.
- The poms will not compile on JDK 17+ until plan phase P3 (Lombok 1.18.16, Guice 4.2/cglib, old plugins).
  Dev machine: Temurin 21 via SDKMAN (`.sdkmanrc`), Temurin 25 also installed; use `./mvnw` (Maven 3.9.16).
  Homebrew JDK 27 is non-LTS: don't target it.
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
cd orienteer-<module> && ../mvnw jetty:run             # run Orienteer + that module on :8080, embedded OrientDB
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

- Java 8 source level until P3 lands (no `var`, records, text blocks yet).
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

- JUnit 4, `@RunWith(OrienteerTestRunner.class)` from the core test-jar (`org.orienteer.junit`). It boots a full
  embedded Orienteer + OrientDB once per JVM using `orienteer-test.properties` → `TestEnvOrienteerWebApplication`.
  Tests are integration tests and share state (test classes are Guice `@Singleton`s).
- `@Inject OrienteerTester tester;` for WicketTester; `@Sudo` to run as admin.
- Surefire excludes `**/*Slow*` (network-dependent tests). Keep this style until the JUnit migration (plan P3).

## Pitfalls

- Don't bump one library in isolation: Wicket/Guice/servlet/Jetty/jakarta move together (plan P5–P6), and
  Hazelcast is pinned to 3.12.x by OrientDB distributed.
- Running or testing creates `runtime/`, `databases/`, `Orienteer/`, `libs/` in the working dir — never commit them.
- Until P3, `orienteer-core` gets two GraalVM lines (25.0.4 via wicket-orientdb, 21.3.5 via `orientdb-core`); 21.3.5 crashes on JDK 22+ (D8).
- `log4j2.xml` ships inside core and every module jar; loader defaults in `orienteer-default.properties` use dead/HTTP repo URLs.
- No real credentials in committed files; the tracked properties hold dev defaults only.
