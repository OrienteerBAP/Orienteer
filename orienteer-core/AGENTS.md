# orienteer-core

The platform itself (about 505 main Java files and 109 Wicket `.html` files). Every other module depends on it.
It also publishes a **test-jar** (`org.orienteer.junit`) that all module tests use. Read the root `AGENTS.md` first.

## Where things are (`src/main/java/org/orienteer/core/`)

| Package | What |
|---|---|
| `OrienteerFilter`, `OrienteerWebApplication`, `OrienteerWebSession` | servlet entry point (with hot reload), Wicket app (`init()` wires everything), session |
| `service` | Guice: `OrienteerInitModule` (ServletModule, properties, app class, ServiceLoader modules), `OrienteerModule` (DB/server/settings bindings), `@OverrideModule` |
| `boot.loader` | dynamic module loader: `OrienteerClassLoader`, Aether-based artifact resolution, `metadata.xml`, Hazelcast distribution of jars |
| `module` | `IOrienteerModule` / `AbstractOrienteerModule`, `ModuledDataInstallator`, the 7 built-in modules (widgets, perspectives, localization, tasks, …) |
| `dao` | Transponder integration: `DAO`, `OrienteerDriver`, `@OrienteerOClass` / `@OrienteerOProperty`; `dao.dm` holds `IOEnum` and `IORestricted` |
| `component` | commands, meta-panels, property editors/viewers, tables, filters, visualizers (`UIVisualizersRegistry`), 31 widgets |
| `widget` | dashboard framework: `@Widget`, `AbstractWidget`, `DashboardPanel`; `support.jquery` (default) and `support.gridster` |
| `method` | `@OMethod` / `@OFilter`: turns annotated code into UI commands (`OMethodsManager`, uses Reflections) |
| `web`, `web.schema` | pages (`@MountPath`): Home, Login, Browse, ODocument(s), Schema/OClass/OProperty/OIndex |
| `tasks` | task framework (`IOTask`, `OTaskManager`, console tasks) |
| `hook` | OrientDB hooks: calculable properties, reference consistency, callbacks |
| `util` | `StartupPropertiesLoader`, `LookupResourceHelper`, `OSchemaHelper` (schema DSL), `OSQLFunctions` (SQL functions with prefix `o`) |
| `wicket.pageStore` | Hazelcast/OrientDB page store for distributed mode (uses Wicket 8 page-store APIs) |
| `resource` | `OContentShareResource` (`/content/...`, Tika + thumbnailator), DB export |

- CSS, JS, images and i18n live in `src/main/resources/org/orienteer/core/**`.
  The main bundle is `OrienteerWebApplication.properties` plus `_ru.utf8` and `_uk.utf8`.
- `src/main/resources/orienteer-default.properties` is **filtered** (`${project.version}`) and holds the defaults.
- `src/main/resources/org/orienteer/core/db.config.xml` and `distributed.db.config.xml` are the embedded OrientDB server configs.
- `config/` holds `hazelcast.xml` (3.9 schema), `hazelcast-docker.xml` (Swarm SPI, whose dependency is commented out),
  the distributed DB config and the backup config.

## Tests

- `src/test/java/org/orienteer/junit/`: `OrienteerTestRunner`, `GuiceTestRunner`, `StaticInjectorProvider`,
  `OrienteerTester`, `@Sudo` / `SudoRule`, and `DistributedModulesTestRunner`.
- `src/test/java/org/orienteer/testenv/TestEnvOrienteerWebApplication` is the application class used in tests.
- `src/test/resources/META-INF/services/com.google.inject.Module` registers test overrides,
  including `FakeHazelcastInitModule`, which uses Mockito.
- About 65 tests. `DependencyManagmentSlowTest` hits the network and is excluded by default.
- Run: `mvn -pl orienteer-core test`, or one class with `-Dtest=DAOTest`.

## Pitfalls

- Changing public APIs here affects every module. Grep the other modules before renaming or removing anything.
- Don't touch `jetty-maven-plugin` config piecemeal. It points at `../orienteer-war/.../web.xml` and
  `jetty.xml` and moves to Jetty 12 in plan P6.
- `dao/AbstractDynamicProvider` calls Guice internals via jOOR, and `hook/CallbackHook` writes a private
  ODocument field. Both are fragile when upgrading Guice or OrientDB.
- `OrienteerEmbeddedStartupListener` registers the Nashorn engine, which is gone since JDK 15, so the code silently does nothing.
- `OrienteerWebApplication.init()` sets `Reflections.log = null`. That field no longer exists in Reflections 0.10+.
- Uses `org.apache.http.util.Args` and commons-* only transitively. Declare them explicitly, or replace them.
- Known bugs are listed in `REFRESH_PLAN.md` P10.
  - Example: `OrienteerInitModule` checks `isAssignableFrom(appClass)` instead of `customAppClass`.

## Upgrade risk: HIGH

This module takes almost every item in plan P3–P7:
- Lombok, Guice, Reflections and Nashorn (P3)
- Aether → Maven Resolver and the dead loader URLs (P4)
- Wicket 9 page store and `Duration`/`Time` (P5). The `org.danekja` lambdas can stay: Wicket 9/10 still ship them
- javax → jakarta across 13 servlet files and `javax.inject` (P6)
- `ModalWindow` in 27 files, removed in Wicket 10 (P5/P6)
- CoreUI 3 → 5 markup (P7)
