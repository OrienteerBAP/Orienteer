# Orienteer Refresh Plan

Living plan for bringing Orienteer from its 2024 state (Java 8, Wicket 8, javax, Jetty 9.4) to current,
supported versions. **Tick items off (`[ ]` → `[x]`) in the same commit/PR that completes them**, and add a
short note (date, PR, deviations) after the item when useful. Module details: see each module's `AGENTS.md`.

Started: 2026-09-24. Versions below were checked on Maven Central on that date — re-check at the start of
every phase (`./mvnw versions:display-dependency-updates versions:display-plugin-updates`).

## Progress overview

| Phase | Goal | Depends on | Status |
|---|---|---|---|
| P0 | AI initialization: AGENTS.md files + this plan | — | done |
| P1 | Baseline: dependencies resolve, reactor consistent, dead scripts removed | external libs (owner) | in progress: done except the 3 external snapshots |
| P2 | Build tooling & Maven hygiene (modern plugins, still Java 8 source) | P1 | todo |
| P3 | Java 21 on the current stack (javax, Wicket 8) — **first green build** | P2, external Stage A | todo |
| P4 | Non-jakarta dependency upgrades (OrientDB, Log4j, Jackson, Tika, Resolver…) | P3 | todo |
| P5 | Wicket 8 → 9 (still javax) | P4, external Stage B | todo |
| P6 | jakarta: Wicket 10, Guice 7, Jetty 12 (ee10), Servlet 6, jakarta.mail | P5, external Stage C | todo |
| P7 | Frontend: CoreUI 5 / Bootstrap 5, icons, JS libs | P4 (can run in parallel with P5/P6) | todo |
| P8 | Feature modules: keep / upgrade / replace / drop decisions and ports | P3+ (per module) | todo |
| P9 | CI/CD, Docker, publishing to Maven Central, archetypes | P3 (minimal CI), P6 (final) | todo |
| P10 | Docs, cleanup, known bugs & security fixes | any time | todo |

## Guiding principles

1. **Keep the build green after every item.** One concern per commit/PR; no big-bang upgrades.
2. **Order:** tooling → JDK → libraries → framework majors → frontend → modules.
3. **Coupled libraries move together:** Wicket ↔ Guice ↔ servlet API ↔ Jetty ↔ jakarta (P5/P6);
   OrientDB distributed ↔ Hazelcast 3.12 (P4). Never bump one of them in isolation.
4. **Automate mechanical changes** where possible (OpenRewrite recipes for javax→jakarta, Java upgrades),
   but review every generated diff.
5. **Tests are the safety net:** add smoke tests before refactoring modules that have none (e.g. graph).
6. **No reformat-only diffs;** match surrounding indentation (see root `AGENTS.md`).

## Decisions log

| # | Date | Decision | Status |
|---|---|---|---|
| D1 | 2026-09-24 | Target `--release 21`; CI also builds/tests on JDK 25. JDK 27 (non-LTS) is not targeted. | accepted |
| D2 | 2026-09-24 | Own libraries (wicket-orientdb, transponder, wicket-console, logger, birt.orientdb, wicket-bpmn-io, wicket-jersey, camel-orientdb) stay **external**, refreshed separately in their own repos. This plan only states what Orienteer needs from them (table below). | accepted |
| D3 | 2026-09-24 | **Park** dead/high-risk modules out of the default build early (bpm, tours, birt, camel, taucharts, graph); decide keep/replace/drop per module in P8. Code stays in git. | accepted |
| D4 | 2026-09-24 | `AGENTS.md` at the root and in every module; this plan lives at `/REFRESH_PLAN.md`. | accepted |
| D5 | 2026-09-24 | Go Wicket 8 → 9 → 10 in two steps (P5, P6) so there is a green javax-only intermediate state. P5 and P6 may be merged if wicket-orientdb jumps straight to Wicket 10. | proposed |
| D6 | 2026-09-24 | Stay on OrientDB 3.2.x (still maintained: 3.2.56 released 2026-09-02). A DB change (YouTrackDB, ArcadeDB…) is out of scope. | proposed |
| D7 | 2026-09-24 | No pre-upgrade JDK 8 test run; the static test inventory (Appendix C) is the reference, and the first real test run is the P3 exit criterion. | accepted |
| D8 | 2026-09-24 | **GraalJS replaces the old GraalVM that OrientDB brings.** OrientDB 3.2.x pulls GraalVM/Truffle 21.3.5, which crashes every embedded OrientDB start on JDK 22+ (`NoSuchMethodError: sun.misc.Unsafe.ensureClassInitialized`). Exclude `org.graalvm.{sdk,truffle,js,tools}` and declare GraalJS 25.0.4 (`polyglot`, `js-scriptengine`, `js` pom runtime). This is the same as wicket-orientdb D11, and it also settles the Nashorn replacement (P3). Revisit when OrientDB upgrades Graal. | accepted (owner, 2026-09-24) |
| D9 | 2026-09-24 | **Versioning and publishing of the external libraries** (the owner delegated the decision). (1) Each library publishes its Stage A as the current snapshot (e.g. wicket-orientdb `2.0-SNAPSHOT`) to the Central Portal snapshots repository, so Orienteer and its CI resolve it without local installs. (2) The library releases that version (e.g. `2.0`) only after Orienteer's P3 build is green against it; fixes found in P3 go into the snapshot first. Orienteer then pins the release. (3) After the release, the library's `master` moves to the next line for Stage B (wicket-orientdb `2.1-SNAPSHOT`); Stage C, being jakarta and API-breaking, gets a new major (wicket-orientdb `3.0`). (4) Stage B/C work before the Stage A release goes on a branch with its own version and never overwrites the snapshot Orienteer uses. Same scheme for transponder (1.1), wicket-console (1.4) and logger (1.4). Namespaces `ru.ydn` and `org.orienteer` must be verified on the Central Portal, with SNAPSHOTs enabled (owner: `ru.ydn` is verified). | accepted (2026-09-24) |
| D10 | 2026-09-25 | Root `orienteer.properties` and `orienteer-test.properties` stay **tracked** (dev/test defaults, dev-only credentials, no real secrets). `.gitignore` re-includes them explicitly and keeps ignoring copies anywhere else. | accepted |

### Open questions (decide when the phase starts; record the answer above)

- [ ] **Distributed mode:** keep OrientDB distributed + Hazelcast 3.12 (EOL) + Hazelcast page store/session filter, or declare single-node only? (affects P4, P5, P6)
- [ ] **CSP policy** for Wicket 9/10: strict nonce-based by default vs. relaxed; how to handle user-supplied JS/markup (pages, taucharts, BIRT). (P5)
- [x] **Tracked config files:** keep `orienteer.properties` / `orienteer-test.properties` tracked (and fix `.gitignore`) or untrack them and ship `*.sample`? (P1) — 2026-09-25: keep tracked → D10
- [ ] **Versioning:** release as `2.0.0` after P6? Keep `groupId org.orienteer`? (P9)
- [ ] **Dynamic module loader:** keep runtime download of modules from Maven repos (security, complexity) or restrict it to a local folder? (P4/P10)

## External dependency requirements (not done in this repo)

Orienteer cannot progress past the stages below until the owning repos deliver. "Stage A/B/C" correspond to
P3, P5, P6. Until they are published (D9: Central Portal snapshot, then a release after Orienteer P3), build them locally with `mvn install` (P1).

| Library (coordinates) | Current in Orienteer | On Central today | Used by | Stage A — P3 (Java 21, javax, Wicket 8) | Stage B — P5 (Wicket 9) | Stage C — P6 (Wicket 10, jakarta) |
|---|---|---|---|---|---|---|
| `ru.ydn.wicket.wicket-orientdb:wicket-orientdb` (+ `test-jar`) | 2.0-SNAPSHOT | 1.5 (2020) | **everything** (core): `OrientDbWebApplication`, REST (`mountOrientDbRestApi`), `ReverseProxyResource` (rproxy), security annotations, prototypes, `LombokExtensions`, `WicketOrientDbTester` | builds & runs on JDK 21 with Wicket 8.15 + OrientDB 3.2.56; Lombok ≥1.18.48; release (or a published snapshot) | ported to Wicket 9.x: `WicketOrientDbTesterScope` (uses the removed `WicketTesterScope`), JUnit 5 for the tester, Servlet 3.1 `ServletInputStream` methods. Estimated at 0.5–1 day | ported to Wicket 10.x, `jakarta.servlet` 6 (`ReverseProxyResource`, `LombokExtensions.asHttpServletRequest` (public API), `WicketOrientDbTester`), `wicket-tester`, OkHttp current (drop the internal `okhttp3.internal.http.HttpMethod`). No Guice/`javax.inject` in the library. Estimated at 1–2 days |
| `org.orienteer.transponder:transponder-orientdb` | 1.1-SNAPSHOT | 1.0 (2021) | core DAO layer (`core.dao`), every module with DAO interfaces | JDK 21 build; bytecode generation lib supports Java 21/25 class files; OrientDB 3.2.56; release | — | — (no servlet coupling expected; verify no `javax.*`) |
| `ru.ydn.wicket.wicket-console:wicket-console` | 1.4-SNAPSHOT | 1.3 (2019) | devutils (→ users, logger-server, standalone) | JDK 21 build on Wicket 8; script engines work without Nashorn | Wicket 9 port | Wicket 10 / jakarta port |
| `org.orienteer:logger` | 1.4-SNAPSHOT | 1.3 (2020) | logger-server | JDK 21 build; release | — | jakarta if it touches servlet/mail APIs |
| `org.orienteer:org.orienteer.birt.orientdb` | 2.0-SNAPSHOT | 1.0 (2018) | birt (parked) | only if P8 keeps BIRT: rebuild on OrientDB 3.2.x + modern BIRT | — | — |
| `org.orienteer.wicket-bpmn-io:wicket-bpmn-io` | 1.0 (Wicket 7.4) | 1.0 | bpm (disabled) | only if P8 keeps BPM | Wicket 9 | Wicket 10 |
| `org.orienteer.wicket-jersey:wicket-jersey` | 1.0 (Jersey 2.32, javax) | 1.0 | tours (disabled) | only if P8 keeps tours **with** JAX-RS | — | Jersey 3/4 (jakarta), Wicket 10 |
| `org.orienteer.camel.component:camel-orientdb` | 1.1 (OrientDB 2.2) | 1.1 | camel (parked) | only if P8 keeps camel: Camel 4 + OrientDB 3.2 | — | — |

- [ ] Stage A delivered: wicket-orientdb, transponder-orientdb, wicket-console, logger resolvable from a real repo (Central or Central Portal snapshots)
  - wicket-orientdb: **Stage A done locally** (2026-09-24, 14 local commits on its `master`, not pushed, not published). Installed in `~/.m2`: parent pom, jar and test-jar `2.0-SNAPSHOT`, Java 21 bytecode. Built against Wicket 8.15, OrientDB 3.2.56, GraalJS 25.0.4, OkHttp 4.9.0, Lombok 1.18.48. 103 tests run, 0 failures, 17 skipped (pre-existing `@Ignore`) on JDK 21 and 25; the JDK 25 run was re-checked independently. No public API changes. Also fixes empty query-backed data providers on OrientDB 3.2.56 (`OQueryModel.iterator`). Full handoff: the "Handoff to Orienteer" section of its `REFRESH_PLAN.md`; the consequences for Orienteer are D8 and P3. Still to do: publishing it (its phase R)
  - wicket-orientdb decisions (owner, 2026-09-24): GraalJS swap accepted; versioning per D9; demo module kept after Stage C; stale remote branches kept for now. Phase R prepared locally (2026-09-24; 23 local commits, not pushed): GitHub Actions CI (JDK 21/25), `central-publishing-maven-plugin` 0.11.0 (server `central`, manual Publish in the portal), release profile (sources, javadoc, GPG), Dependabot, JaCoCo; build green on 21/25. Waiting for the owner: Central Portal token in `~/.m2/settings.xml` (server `central`), SNAPSHOTs enabled for `ru.ydn`, approval to push and deploy the snapshot (`./mvnw -B clean deploy -Prelease -Dgpg.skip`); a GPG key only for the later `2.0` release. Runbook: the library's `REFRESH_PLAN.md`
  - transponder-orientdb, wicket-console, logger: not started
- [ ] Stage B delivered: wicket-orientdb, wicket-console on Wicket 9
- [ ] Stage C delivered: wicket-orientdb, wicket-console (+ logger if needed) on Wicket 10 / jakarta

---

## P0 — AI initialization

- [x] Analyze the repository: modules, dependencies, infra, branches, blockers — 2026-09-24
- [x] Root `AGENTS.md` (stack, build status, commands, architecture, conventions, testing, pitfalls) — 2026-09-24
- [x] `AGENTS.md` in all 23 modules (purpose, entry points, deps, tests, pitfalls, upgrade risk) — 2026-09-24
- [x] This plan with decisions log, external requirements and version appendix — 2026-09-24
- [x] Review and commit the P0 files (owner) — 2026-09-24

## P1 — Baseline: make the reactor resolvable and consistent

Goal: every non-parked module resolves its dependencies (`./mvnw dependency:tree` reports no missing POMs), and nothing
in the repo points at dead infrastructure. The build is not expected to compile on JDK 21 yet (that is P3).

- [ ] Make the external libraries resolvable (outside this repo, D9): wicket-orientdb 2.0-SNAPSHOT (done 2026-09-24: Stage A, published to the Central Portal snapshots), transponder-orientdb 1.1-SNAPSHOT, wicket-console 1.4-SNAPSHOT, logger 1.4-SNAPSHOT
- [x] Move the transponder version into a root property (`transponder.version`) next to `wicket.orientdb.version`
- [x] Remove dead repositories from the root pom: `bintray` (jcenter) and `oss.sonatype.org` snapshots; add `https://central.sonatype.com/repository/maven-snapshots/` (snapshots only, releases disabled), where the external libraries publish their Stage A snapshots (D9) — 2026-09-25. The OSSRH `distributionManagement`/nexus-staging (P9) and the archetype-war template repository (P9) remain
- [x] Park modules (D3): move `orienteer-birt`, `orienteer-camel`, `orienteer-taucharts`, `orienteer-graph` out of the `default-modules` profile into a new opt-in profile `parked` together with `orienteer-bpm` and `orienteer-tours` (so `./mvnw -Pparked …` can still try them) — 2026-09-25; `./mvnw -Pparked validate` passes
- [x] `orienteer-standalone`: drop the dependencies on parked modules (`orienteer-bpm`, `orienteer-graph`) — 2026-09-25; no other default module depends on a parked one
- [x] Remove broken scripts and images: `build.sh`, `run.sh`, `Dockerfile_ibmjdk` (they reference the removed `orienteer-object` and the no-longer-produced `jetty-runner.jar`) — 2026-09-25
- [x] Add the Maven Wrapper (`mvn wrapper:wrapper -Dmaven=3.9.x`), commit `mvnw`, `mvnw.cmd`, `.mvn/wrapper/` — 2026-09-25: wrapper 3.3.4, Maven 3.9.16, script-only
- [x] Add `.sdkmanrc` (`java=21.0.11-tem`, the locally installed Temurin 21) and a minimal `.editorconfig` (UTF-8, LF — **no mass reformat**) — 2026-09-25. No indent style is set: measured, Java is 556 tab / 447 space files and XML/HTML/JS are mostly spaces, so there is no majority to enforce
- [x] `.gitignore`: resolve the open question on tracked properties; add `.DS_Store`, `.vscode/`, `runtime/`, `libs/` — 2026-09-25: root properties re-included explicitly (D10)
- [x] Confirm the test inventory (Appendix C) by listing tests per module (`@Test` / `@Ignore` counts) — 2026-09-24 (counted with `rg`)
- [ ] Exit check: `./mvnw -fn dependency:tree` reports no missing POM for any non-parked module. Status 2026-09-25: all 18 reactor projects pass `validate`; `dependency:tree` reports exactly 3 missing POMs: `transponder-orientdb:1.1-SNAPSHOT`, `wicket-console:1.4-SNAPSHOT`, `logger:1.4-SNAPSHOT`. `dependency:go-offline` / `resolve-plugins` can't run until P2 removes cobertura 2.7, which needs JDK 8's `tools.jar`

## P2 — Build tooling & Maven hygiene

Goal: modern, centrally managed plugins that also work on JDK 21, without changing the Java level yet.

- [ ] Pin every plugin in root `<pluginManagement>` at current versions (Appendix B); remove per-module plugin/version overrides (compiler/surefire/jar in etl, metrics, rproxy, taucharts, tours; assembly/jar/deploy in standalone; war/deploy in war; bundle 2.3.6 in 14 modules)
- [ ] Remove dead plugins: `maven-eclipse-plugin` (root + 14 modules), `org.eclipse.m2e:lifecycle-mapping`, `cobertura-maven-plugin`, `coveralls-maven-plugin` — cobertura 2.7 also blocks `dependency:go-offline` on JDK 9+ (needs `com.sun:tools`)
- [ ] Drop `<type>bundle</type>` from the OrientDB dependencies and remove the `maven-bundle-plugin` build extension (verify the artifacts resolve as plain jars)
- [ ] Import BOMs in `dependencyManagement`: `jackson-bom`, `log4j-bom`, `junit-bom`, `mockito-bom` (Jetty BOM comes in P6)
- [ ] Move module-local third-party versions into root properties/`dependencyManagement` (scribejava, prometheus, retrofit, rxjava, mxgraph, pivottable/d3/c3, taucharts, POI, Camel…)
- [ ] Remove unused properties: `hazelcast.version` (overridden transitively by OrientDB), `hazelcast-wm.version`, `docker-client.version`, `wtp.version`, `twilio.version` (twilio module); remove unused managed deps (`docker-client`, `hazelcast-docker-swarm-discovery-spi`) or move them next to their use
- [ ] Add `maven-enforcer-plugin`: `requireMavenVersion [3.9,)`, `requireJavaVersion [21,)` (enable in P3), `banDuplicatePomDependencyVersions`, `dependencyConvergence` (report-only first)
- [ ] Checkstyle: plugin 3.6.x + current Checkstyle; migrate `check_style.xml` (DTD 1.3, move `LineLength` out of `TreeWalker`, `JavadocMethod` `scope` → `accessModifiers`); keep `failOnViolation`
- [ ] Add `jacoco-maven-plugin` (report only, no thresholds yet)
- [ ] Fix the `fulltest` profile so it really includes `*Slow*` tests (override the inherited `excludes`)
- [ ] Configure `versions-maven-plugin` with a rules file that ignores alpha/beta/RC/milestone versions
- [ ] Exit check: same modules build and test as before P2 (on whichever JDK P1 used), with no plugin warnings about JDK incompatibility

## P3 — Java 21 on the current stack (javax, Wicket 8)

Goal: `./mvnw verify` green on JDK 21 **and** 25, app starts with `jetty:run`. First real test run.

- [ ] Lombok 1.18.16 → latest (≥1.18.48); declare it in `maven-compiler-plugin` `annotationProcessorPaths` (required from JDK 23, harmless before); add root `lombok.config` (`config.stopBubbling = true`, `lombok.addLombokGeneratedAnnotation = true`)
- [ ] Guice 4.2.0 → 6.0.0 (+ `guice-servlet` 6.0.0): no cglib, still `javax.inject`/`javax.servlet`; fix `core/dao/AbstractDynamicProvider` (jOOR access to Guice internals `InjectorImpl.enterContext`)
- [ ] Wicket 8 IoC lazy proxies (cglib) on JDK 17+: check whether they need `--add-opens` and add them (goes away with Wicket 9/ByteBuddy in P5)
- [ ] Remove the ASM 7.1 pin (check who needs it: `./mvnw dependency:tree -Dincludes=org.ow2.asm`) or bump to ≥9.8. ASM 7.1 can't read Java 21+ class files (e.g. BouncyCastle 1.85, which OrientDB 3.2.56 brings)
- [ ] Align OrientDB with wicket-orientdb Stage A: 3.2.27 → **3.2.56** in P3 (not P4), because that's what the library is built and tested against. Transitive changes: `commons-lang` 2.6 dropped, lz4 moves to `at.yawk.lz4` 1.11.0 (same packages), BouncyCastle 1.85 added, jackson-core 2.22. Breaking behavior: a closed `OResultSet` now yields nothing, so always collect before the try-with-resources closes it (wicket-orientdb checked the 12 Orienteer files that use `OResultSet` and found no such pattern; re-check when writing new code)
- [ ] GraalJS per D8: add the `org.graalvm.{sdk,truffle,js,tools}` exclusions to the direct `orientdb-core` declaration in root `dependencyManagement`, and manage GraalJS 25.0.4 (`polyglot`, `js-scriptengine`, `js` pom). Then check that `./mvnw dependency:tree -Dincludes='org.graalvm*'` shows only 25.0.4; other OrientDB artifacts (server, distributed, tools, etl, graphdb) may need the same exclusions
- [ ] Mockito 2.22 → 5.x; JUnit 4.13.1 → 4.13.2
- [ ] Surefire 3.x `argLine` with the empirically required `--add-opens`/`--add-exports` (OrientDB, Hazelcast, cglib); mirror them in the Jetty plugin JVM args and Docker `JAVA_OPTIONS`; document the list in root `AGENTS.md`
- [ ] JDK 24+: check `sun.misc.Unsafe` memory-access warnings from OrientDB/Hazelcast/Netty; add `--sun-misc-unsafe-memory-access=allow` where needed and note it
- [ ] Reflections 0.9.10 → ClassGraph (preferred) or Reflections 0.10.2 in `core/method/MethodStorage`; remove `Reflections.log = null` in `OrienteerWebApplication`
- [ ] Nashorn replacement: settled by D8 (GraalJS 25.0.4). Remove or adapt the Nashorn registration in `OrienteerEmbeddedStartupListener`; verify server-side JS in pages, devutils consoles and OrientDB JS functions
- [ ] Jetty 9.4.12 → last 9.4.x (9.4.58) as a stopgap for the dev plugin and standalone until P6. On JDK 25 the plugin also needs `org.ow2.asm:asm` ≥9.8 (wicket-orientdb uses 9.10.1) as a plugin dependency; otherwise annotation scanning fails on Java 25 class files
- [ ] Compiler: `maven.compiler.release=21` (replace `source/target`), keep `-parameters`; remove module overrides (metrics, rproxy, tours hardcode 1.8)
- [ ] Declare used-but-undeclared deps (`./mvnw dependency:analyze`): replace `org.apache.http.util.Args` (core ×12, architect ×7, notification) with Wicket `Args`/`Objects`; declare commons-io/lang3/collections4, okhttp where used directly
- [ ] Add JUnit Jupiter + `junit-vintage-engine` so new tests can use Jupiter while `OrienteerTestRunner` tests keep running
- [ ] Minimal CI (full CI/CD is P9): GitHub Actions on push + PR, matrix JDK 21/25, `./mvnw -B verify`
- [ ] Enable enforcer `requireJavaVersion [21,)`
- [ ] Update root `AGENTS.md` (build status, Java level, conventions: modern Java allowed)
- [ ] Exit check: `./mvnw verify` green on 21 and 25; test results match Appendix C (differences explained); `jetty:run` in core shows the login page and the embedded DB starts

## P4 — Non-jakarta dependency upgrades

Goal: everything that doesn't require jakarta or a Wicket major is current.

- [ ] OrientDB: the move to 3.2.56 happens in P3 (alignment with wicket-orientdb). Here, take newer 3.2.x patches if any; re-test etl (subclasses ETL internals), pages (`OrientDBInternal` script manager), hooks, `CallbackHook` private-field access
- [ ] Hazelcast (via OrientDB distributed, 3.12.13): update `orienteer-core/config/hazelcast*.xml` schema 3.9 → 3.12; replace deprecated `ILock` in `ReloadOrienteerTask`; decide on the Swarm discovery config (its SPI dependency is commented out)
- [ ] Log4j 2.17.1 → 2.26.x via BOM; `log4j-slf4j-impl` → `log4j-slf4j2-impl` + SLF4J 2.x; stop shipping `log4j2.xml` and the logging binding in library jars (keep them only in war, standalone and test scope)
- [ ] Jackson 2.12.1 → 2.22.x (BOM)
- [ ] Tika 1.22 → 3.x (`tika-core`); thumbnailator 0.4.14 → 0.4.21; `joor-java-8` → `joor` 0.9.15 or remove jOOR usages
- [ ] Dynamic loader: Eclipse Aether 1.1.0 + `maven-aether-provider` 3.3.9 → Maven Resolver 2.x (`maven-resolver-supplier-mvn3`); drop plexus deps
- [ ] Loader defaults in `orienteer-default.properties`: `http://repo1.maven.org` → `https://repo.maven.apache.org/maven2/`; remove the OSSRH entries; reconsider jitpack
- [ ] XXE hardening in `core/boot/loader/internal/AbstractXmlHandler` (disallow DOCTYPE / external entities)
- [ ] Move `orientqb` from core to bpm (only bpm uses it)
- [ ] Mail interim: `javax.mail:mail` 1.4.7 → `com.sun.mail:javax.mail` 1.6.2 (same `javax.mail` package; jakarta in P6)
- [ ] twilio: Retrofit 2.7.2 → 2.12/3.x, RxJava 2 → 3 (or drop Rx), OkHttp 4+
- [ ] metrics: Prometheus `simpleclient` 0.8.1 → 0.16.0 (or migrate to `prometheus-metrics-*` 1.x)
- [ ] users: scribejava 6.5.1 → 8.3.x
- [ ] Webjars without breaking changes: jQuery 3.4.1 → 3.7.1 (CVE fixes), jQuery UI 1.12.1 → 1.14.x, pace, bootstrap-datepicker latest
- [ ] Add `.github/dependabot.yml` (maven + github-actions, weekly, grouped); ignore majors of Wicket/Guice/Jetty/servlet/CoreUI until their phases
- [ ] Exit check: green build; `versions:display-dependency-updates` shows only the majors planned for P5–P8

## P5 — Wicket 8 → 9 (still javax)

Goal: Wicket 9.24 (last 9.x), green build, main pages work. Migration guide:
https://cwiki.apache.org/confluence/display/WICKET/Migration+to+Wicket+9.0

- [ ] Prerequisite: external Stage B (wicket-orientdb, wicket-console on Wicket 9)
- [ ] Wicket 8.15 → 9.24; wicketstuff-select2 → 9.x; wicket-webjars 2.0.15 → 3.0.x
- [ ] Page store rewrite for the new Wicket 9 `IPageStore` chain: `core/wicket/pageStore/*` (`HazelcastPageStore`, `HazelcastPagesCache`, `OrientDbDataStore`) and the `PageManagerProvider` in `OrienteerWebApplication` (or drop it — see open question on distributed mode)
- [ ] `org.danekja` serializable lambdas (14 files): **no migration required**. wicket-core 9.24 and 10.11 still depend on `org.danekja:jdk-serializable-functional` (verified on Central, 2026-09-24). Just check that the imports compile; switching to `org.apache.wicket.lambda` is optional cleanup
- [ ] Wicket 9's `WicketTester` asserts with JUnit 5: put `junit-jupiter-api` on the test classpath (the tests still run under the JUnit 4 `OrienteerTestRunner` via vintage). `WicketTesterScope` is removed in 9, but Orienteer doesn't use it (only `WicketOrientDbTester`)
- [ ] `org.apache.wicket.util.time.Duration`/`Time` → `java.time` (core `OrienteerWebjarsSettings`, `OContentShareResource`; metrics `OMetricsResource`; users `RestorePasswordResource`)
- [ ] `ModalWindow` → `ModalDialog` (deprecated in 9, **removed in 10**): introduce the replacement in `AbstractModalWindowCommand`, then migrate core (27 files), architect, users, graph
- [ ] CSP (Wicket 9 enables a strict CSP by default): decide the policy (open question), relax temporarily if needed, then fix inline scripts/`eval` and re-tighten
- [ ] Go through the remaining migration-guide items (request cycle listeners, `IResourceSettings`, markup/header item changes) and fix compile errors/deprecations
- [ ] Exit check: green build on 21/25; manual smoke: login, browse class, edit document, schema pages, dashboards + widget add/edit, module install/reload

## P6 — jakarta: Wicket 10, Guice 7, Jetty 12 (ee10), Servlet 6

Goal: no `javax.servlet`/`javax.inject`/`javax.mail` left; runs on Jetty 12 ee10 (and any Servlet 6 container).

- [ ] Prerequisite: external Stage C (wicket-orientdb, wicket-console, logger if needed)
- [ ] Run OpenRewrite `org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta` (`rewrite-migrate-java`) for package renames; review the diff
- [ ] Wicket 9 → 10.x (10.11+), wicketstuff 10.x, wicket-webjars 4.x; add `org.apache.wicket:wicket-tester` (test scope) for `WicketTester`
- [ ] Guice 6 → 7.0.0 + `guice-servlet` 7: `javax.inject` → `jakarta.inject` (core, users, graph `@RequestScoped`, tests)
- [ ] `javax.servlet` → `jakarta.servlet` (core 13 files, logger-server, users, standalone); `jakarta.servlet-api` 6.0 provided
- [ ] `web.xml` (war, standalone, archetype-war template) → Jakarta EE 10 `web-app_6_0.xsd`
- [ ] Jetty 12: replace `jetty-maven-plugin` in 20 modules with `org.eclipse.jetty.ee10:jetty-ee10-maven-plugin`, configured **once** in root `pluginManagement`; import `jetty-bom`/`jetty-ee10-bom`; update `jetty.xml` (DTD 10.0) and `jetty-context.xml` (`org.eclipse.jetty.ee10.webapp.WebAppContext`); drop the Jetty 7 `SelectChannelConnector` config
- [ ] Standalone on Jetty 12: `jetty-server` + `jetty-ee10-webapp` (no `jetty-all` any more), or `ServletContextHandler` + `FilterHolder(OrienteerFilter)` in a shaded jar; drop `Resource.setDefaultUseCaches`
- [ ] Hazelcast web-session filter (`com.hazelcast.web.WebFilter`, bound reflectively in `OrienteerInitModule`) and `jetty-hazelcast` in war: remove or replace per the distributed-mode decision
- [ ] Mail: `jakarta.mail-api` 2.1 + `org.eclipse.angus:angus-mail` 2.0 (mail, notification, logger-server)
- [ ] `ModalWindow` fully gone (compile check)
- [ ] Enforcer `bannedDependencies`: `javax.servlet:*`, `javax.inject:javax.inject`, `javax.mail:*`, `javax.activation:*`, `org.eclipse.jetty.aggregate:*`
- [ ] Exit check: green on 21/25; `jetty:run` (ee10) works; `orienteer.war` deploys on Jetty 12 ee10 and Tomcat 10.1+/11

## P7 — Frontend refresh

Can start after P4 and run in parallel with P5/P6 (markup changes are mostly independent of Java APIs).

- [ ] CoreUI 3.4 → 5.x (Bootstrap 5.3): rewrite layout markup (`BasePage`, `OrienteerBasePage`, sidebar/header), `data-toggle` → `data-bs-toggle`; remove the Bootstrap 4 webjar, `tether`, and the CoreUI 2 leftovers in `BasePage.getBodyAppSubClasses()`
- [ ] Icons: Font Awesome 4.7 → current Font Awesome Free (regenerate the 833-line `FAIconType` enum) or Bootstrap Icons; remove simple-line-icons
- [ ] Dashboards: gridster.js 0.5.6 (abandoned) → gridstack.js, or drop the gridster support and keep the jQuery UI one
- [ ] bootstrap-datepicker → native `<input type="date">` or a Bootstrap 5 picker; remove vendored `bootstrap-tabdrop.js`
- [ ] CodeMirror 5.27 → 5.65.x (drop-in) — CodeMirror 6 only if there is a reason
- [ ] select2 with a Bootstrap 5 theme
- [ ] Remove IE shims loaded from maxcdn in `BasePage.html`; review `webjars.useCdnResources` defaults
- [ ] Exit check: no Bootstrap 4/CoreUI 3 classes left (`rg 'c-app|data-toggle='`); key pages checked in current Chrome, Firefox, Safari

## P8 — Feature modules

Fill in the decision column when the module is picked up; record it in the decisions log too.

| Module | Parked? | Options | Decision |
|---|---|---|---|
| bpm | yes (disabled) | drop · rewrite on another engine (Flowable, Camunda 8 API) · native Orienteer workflows | _tbd_ |
| tours | yes (disabled) | drop · rewrite without JAX-RS + driver.js 1.x | _tbd_ |
| birt | yes | drop · upgrade to modern BIRT (needs birt.orientdb) · replace (e.g. JasperReports) | _tbd_ |
| camel | yes | drop · port to Camel 4 (new OrientDB component) | _tbd_ |
| taucharts | yes | drop · replace chart lib (e.g. ECharts) | _tbd_ |
| graph | yes | port to native `OVertex`/`OEdge` · drop | _tbd_ |
| architect | no | keep mxGraph 4.2.2 (archived) · port to maxGraph | _tbd_ |
| others | no | keep & upgrade | keep |

- [ ] **bpm:** decide; if dropped remove module, `modules.xml` entry, archetype/standalone references; if kept, plan the rewrite separately
- [ ] **tours:** decide; if kept: serve tours via a Wicket resource instead of JAX-RS, driver.js 1.x, remove vendored Bootstrap Tourist
- [ ] **birt:** decide; if kept: modern BIRT runtime, rebuilt OrientDB driver, regenerate the golden HTML, fix credential propagation into the report datasource, CSP
- [ ] **camel:** decide; if kept: Camel 4.x, drop `camel-xmljson`/json-lib/XStream, align POI 5.x, new `orientdb` component
- [ ] **taucharts:** decide; if kept: replace the library, remove `eval()`
- [ ] **graph:** add smoke + create vertex/edge tests first; replace Blueprints with native API (drops orientdb-graphdb, Groovy, jettison); fix `<finalName>`; un-park
- [ ] **architect:** `ModalWindow` → `ModalDialog` (P5); mxGraph 3.7.4 → 4.2.2 or maxGraph; CSP review of inline JS
- [ ] **pivottable:** pivottable 2.4 → 2.23, d3/c3 current; escape stored config (P10)
- [ ] **pages / etl:** re-verify after every OrientDB bump; add an ETL round-trip test using the unused `config.json`/`source.csv` fixtures
- [ ] **devutils / logger-server:** follow external Stage A–C; no local work beyond jakarta renames
- [ ] **users:** scribejava 8 (P4), `ModalDialog`, `java.time` (P5), `jakarta.inject` (P6)
- [ ] **mail / notification / twilio:** re-enable ignored tests with fakes (e.g. GreenMail for SMTP, a stub HTTP server for Twilio)
- [ ] **metrics:** optional migration to Prometheus client 1.x; re-check OrientDB issue #9169 TODO
- [ ] **rproxy:** fix headers/cookies bug (P10); follow wicket-orientdb `ReverseProxyResource`

## P9 — CI/CD, Docker, publishing, archetypes

- [ ] GitHub Actions: current majors of `actions/checkout` / `actions/setup-java` (`distribution: temurin`, `cache: maven`); push + PR triggers; JDK 21/25 matrix; upload surefire reports; `concurrency` to cancel superseded runs
- [ ] Remove `.travis.yml`
- [ ] Docker: multi-stage `Dockerfile` with a `maven:3.9-eclipse-temurin-21` builder and an official Jetty 12 JDK 21 runtime (or `eclipse-temurin:21-jre` + standalone); non-root; multi-arch amd64/arm64 via buildx; review `-XX:MaxDirectMemorySize=512g`; merge or delete `Dockerfile.mvn`; stop depending on the custom `orienteer/jetty` image
- [ ] Remove `com.spotify:dockerfile-maven-plugin` from `orienteer-war`
- [ ] Publishing: replace OSSRH `distributionManagement` and `nexus-staging-maven-plugin` with `org.sonatype.central:central-publishing-maven-plugin`; verify the `org.orienteer` namespace on the Central Portal; GPG signing in CI; rename secrets; keep or drop the `github` Packages profile
- [ ] Release process: maven-release-plugin 3.x (or CI-driven `versions:set` + tag); decide the first release version (open question)
- [ ] Archetypes: update both templates (Java 21, jakarta, Jetty 12 ee10, JUnit, plugin versions, repositories, Dockerfile), remove `orienteer-object`, register archetype-war `.gitignore`, align archetype plugin versions (2.3/2.2 vs 3.0.1 → 3.4.x); run archetype ITs in CI
- [ ] Coverage and security in CI: JaCoCo report, CodeQL (Java) workflow, Dependabot alerts

## P10 — Docs, cleanup, known bugs & security

### Docs & cleanup
- [ ] README: remove Travis/GitPitch badges, Java 21 prerequisite, build steps incl. external libs, supported containers (Servlet 6), current Docker usage
- [ ] Remove `PITCHME.md`, `PITCHME.yaml`, `Procfile`, `system.properties` (Heroku, Java 7)
- [ ] `modules.xml`: remove `orienteer-object`, `orienteer-bot`; add `orienteer-notification`, `orienteer-twilio`; reflect P8 decisions; consider versioning it instead of reading GitHub `master`
- [ ] Delete dead duplicate `IInitializer` files (birt, etl, rproxy, taucharts under `src/main/resources/org/...`); move birt `new_report.rptdesign` out of `META-INF/services`
- [ ] Remove commented-out dependency blocks (metrics, tours poms; Hazelcast block in core pom)
- [ ] Delete stale remote branches (all fully merged: `develop`, `future`, `wicket8`, `coreui_update`, `hotfixes-1.2`, `bpm`, old `dependabot/*`, `gitpitch`) — owner decision
- [ ] Keep all `AGENTS.md` files current at the end of every phase

### Known bugs
- [ ] `core/service/OrienteerInitModule`: `isAssignableFrom(appClass)` should test `customAppClass`, so an invalid custom application class is never detected
- [ ] `OrienteerInitModule`: `e.printStackTrace()` → logger; `bindHazelcastFilter` puts `map-name` twice
- [ ] `OrienteerFilter.destroy()` NPE when the inner filter is null
- [ ] `OrienteerWebApplication.registerModule` calls `getServiceInstance` twice (non-singleton modules get two instances)
- [ ] rproxy `ORProxyResource` constructor puts configured headers into `cookies`
- [ ] standalone `StartStandalone` `--wait` loop never re-reads `line`
- [ ] graph pom `<finalName>orienteer</finalName>` (copy-paste)
- [ ] war `jetty:run` references a `jetty-context.xml` that only exists in core

### Security
- [ ] taucharts: `eval()` of stored config/data and verbatim `postProcess` JS (stored XSS)
- [ ] pivottable: unescaped `JSON.parse('${config}')` in `pivottable.tmpl.js`
- [ ] XXE in loader XML parsing (done in P4 — tick both)
- [ ] birt writes the session username/password into the report datasource; camel stores the session password in context globals
- [ ] logger-server receiver `/resource/ologger` accepts unauthenticated requests — document or add a token
- [ ] etl loader connects as DB admin
- [ ] Default credentials (admin/admin, reader/reader, root/root; Hazelcast group orienteer/orienteer): warn loudly at startup when defaults are used with `orienteer.production=true`

---

## Appendix A — Dependency versions (current → target)

"Latest" = latest stable on Maven Central on 2026-09-24. Re-check when the phase starts.

| Dependency | Current | Latest seen | Target / note | Phase |
|---|---|---|---|---|
| JDK (`release`) | 1.8 | 25 LTS (27 GA, non-LTS) | 21, CI also 25 | P3 |
| Apache Wicket (+ extensions, guice, devutils) | 8.15.0 | 10.11.0 (9.x: 9.24.0) | 9.24 → 10.x | P5/P6 |
| wicketstuff-select2 | 8.15.0 | 10.11.0 | follows Wicket | P5/P6 |
| wicket-webjars | 2.0.15 | 4.0.15 (3.0.8 for Wicket 9) | 3.x → 4.x | P5/P6 |
| Guice / guice-servlet | 4.2.0 | 7.0.0 | 6.0.0 (P3) → 7.0.0 (P6) | P3/P6 |
| Servlet API | javax 3.0.1 / 3.1.0 | jakarta 6.1.0 | jakarta 6.0 (Wicket 10 baseline) | P6 |
| Jetty (plugin, standalone) | 9.4.12.v20180830 | 12.1.13 (9.4.58 last 9.4) | 9.4.58 stopgap → 12.1.x ee10 | P3/P6 |
| OrientDB (core, client, server, distributed, tools, etl, graphdb) | 3.2.27 | 3.2.56 | 3.2.56 (aligned with wicket-orientdb), later patches in P4 | P3/P4 |
| Hazelcast | 3.x via OrientDB (property says 3.9.4) | pinned 3.12.13 by OrientDB 3.2.56 | follow OrientDB | P4 |
| GraalVM/GraalJS (via OrientDB) | 21.3.5 (crashes on JDK 22+) | 25.0.4 | 25.0.4 via exclusions (D8) | P3 |
| Log4j 2 | 2.17.1 | 2.26.1 | latest 2.x, `log4j-slf4j2-impl` | P4 |
| SLF4J | 1.7 (transitive) | 2.0.20 | 2.0.x | P4 |
| Jackson | 2.12.1 | 2.22.3 | latest 2.x via BOM | P4 |
| Lombok | 1.18.16 | 1.18.48 | latest | P3 |
| JUnit 4 / Jupiter | 4.13.1 / — | 4.13.2 / 6.1.3 (+ vintage 6.1.3) | 4.13.2 + Jupiter/vintage | P3 |
| Mockito | 2.22.0 | 5.24.0 | 5.x | P3 |
| ASM (pin) | 7.1 | 9.10.1 | remove pin / ≥9.8 (also a Jetty 9.4 plugin dependency on JDK 25) | P3 |
| Reflections | 0.9.10 | 0.10.2 (unmaintained) | ClassGraph 4.8.x | P3 |
| Nashorn | JDK built-in (gone since 15) | nashorn-core 15.7 | GraalJS or nashorn-core | P3 |
| jOOR | joor-java-8 0.9.12 | joor 0.9.15 | joor or remove | P4 |
| Eclipse Aether / maven-aether-provider | 1.1.0 / 3.3.9 | Maven Resolver 2.0.23 | Resolver 2.x | P4 |
| Apache Tika (core) | 1.22 | 4.0.0 (3.3.2) | 3.x | P4 |
| thumbnailator | 0.4.14 | 0.4.21 | latest | P4 |
| javax.mail → jakarta.mail | javax.mail:mail 1.4.7 | jakarta.mail-api 2.1.5 + angus-mail 2.0.5 | 1.6.2 (P4) → jakarta (P6) | P4/P6 |
| scribejava (users) | 6.5.1 | 8.3.3 | 8.3.x | P4 |
| Prometheus client (metrics) | simpleclient 0.8.1 | 0.16.0 / prometheus-metrics 1.9.0 | 0.16 or 1.x | P4/P8 |
| Retrofit / RxJava (twilio) | 2.7.2 / 2.2.19 | 3.0.0 / rxjava3 3.1.12 | current | P4 |
| Camel (camel) | 2.25.2 (+ xmljson 2.23.4) | 4.22.1 | P8 decision | P8 |
| Apache POI (camel) | 3.17 / 3.15 mixed | 5.5.1 | P8 decision | P8 |
| Camunda BOM (bpm) | 7.5.0 | 7.24.0 (CE EOL) | P8 decision | P8 |
| BIRT runtime (birt, via birt.orientdb) | 4.4.2 | — | P8 decision | P8 |
| CoreUI (webjar) | 3.4.0 | 5.5.0 | 5.x | P7 |
| Bootstrap (webjar) | 4.3.1 (unused) | 5.3.8 | via CoreUI 5 | P7 |
| Font Awesome (webjar) | 4.7.0 | 7.3.0 | current Free | P7 |
| jQuery / jQuery UI (webjar) | 3.4.1 / 1.12.1 | 3.7.1 (4.0.0) / 1.14.2 | 3.7.1 / 1.14.x | P4 |
| CodeMirror (webjar) | 5.27.4 | 5.65.21 (6.x) | 5.65.x | P7 |
| gridster.js | 0.5.6 | abandoned | gridstack.js 12.x or drop | P7 |
| mxGraph (architect) | 3.7.4 | 4.2.2 (archived) | P8 decision | P8 |
| driver.js (tours) | 0.9.8 | 1.4.0 | P8 decision | P8 |
| taucharts (taucharts) | 1.2.2 | abandoned | P8 decision (e.g. ECharts 6.x) | P8 |
| pivottable / d3 / c3 | 2.4.0 / 3.5.17 / 0.4.11 | 2.23 / … | P8 | P8 |

## Appendix B — Maven plugins (current → latest seen 2026-09-24)

| Plugin | Current | Latest seen | Action (phase) |
|---|---|---|---|
| maven-compiler-plugin | 3.7.0 | 3.16.0 | bump, `release` (P2/P3) |
| maven-surefire-plugin | 2.20 / 2.22.1 | 3.6.0 | bump (P2) |
| maven-jar-plugin | 3.0.2 / 2.5 | 3.5.1 | bump (P2) |
| maven-war-plugin | 3.1.0 | 3.5.1 | bump — <3.3.1 fails on JDK 16+ (P2) |
| maven-assembly-plugin (standalone) | 2.5.3 | 3.8.0 | bump or replace with shade 3.6.2 (P2/P6) |
| maven-deploy-plugin | 2.7 | 3.2.0 | bump (P2) |
| maven-release-plugin | 2.5.3 | 3.3.1 | bump (P9) |
| maven-source-plugin | 3.0.0 | 3.4.0 | bump (P9) |
| maven-javadoc-plugin | 2.10.3 | 3.12.0 | bump (P9) |
| maven-gpg-plugin | 1.6 | 3.2.8 | bump (P9) |
| maven-checkstyle-plugin | 3.0.0 | 3.6.0 (Checkstyle 14.x) | bump + config migration (P2) |
| maven-archetype-plugin / archetype-packaging | 3.0.1 & 2.3 / 3.0.1 & 2.2 | 3.4.1 | align (P2/P9) |
| maven-dependency-plugin (birt) | unversioned | — | pin (P2) |
| maven-enforcer-plugin | — | 3.6.3 | add (P2) |
| jacoco-maven-plugin | — | 0.8.15 | add (P2) |
| versions-maven-plugin | — | 2.22.0 | add config (P2) |
| maven-wrapper-plugin | — | 3.3.4 | wrapper (P1) |
| org.apache.felix:maven-bundle-plugin | 3.0.1 / 2.3.6 | 6.2.0 | remove with `type=bundle` (P2) |
| jetty-maven-plugin | 9.4.12 | → jetty-ee10-maven-plugin 12.1.13 | replace (P6) |
| maven-eclipse-plugin | 2.10 / 2.9 | retired | remove (P2) |
| m2e lifecycle-mapping | 1.0.0 | IDE-only | remove (P2) |
| cobertura-maven-plugin | 2.7 | dead | remove → JaCoCo (P2) |
| coveralls-maven-plugin | 4.2.0 | abandoned | remove (P2) |
| nexus-staging-maven-plugin | 1.6.7 | OSSRH gone | → central-publishing-maven-plugin 0.11.0 (P9) |
| com.spotify:dockerfile-maven-plugin | 1.4.10 | archived | remove (P9) |
| rewrite-maven-plugin (+ rewrite-migrate-java) | — | 6.46.1 (3.42.1) | use ad hoc (P3/P6) |

## Appendix C — Test inventory (static analysis, 2026-09-24; confirm in P1/P3)

| Module | Test classes | `@Test` | `@Ignore` | Notes |
|---|---|---|---|---|
| core | 20 | 65 | 4 | `DependencyManagmentSlowTest` excluded (network) |
| architect | 3 (+2 helpers) | 7 | 0 | 6 plain JUnit, 1 integration |
| birt | 1 | 1 | 0 | golden-HTML compare (parked) |
| bpm | 1 (+1 helper) | 14 | 0 | disabled |
| camel | 1 | 1 | 0 | smoke (parked) |
| devutils | 1 | 3 | 0 | |
| etl | 1 | 1 | 0 | smoke |
| graph | 0 | 0 | 0 | **no tests** (parked) |
| logger-server | 4 (+2 helpers) | 7 | 0 | |
| mail | 2 (+2 helpers) | 6 | 3 | |
| metrics | 1 | 1 | 0 | smoke |
| notification | 4 (+8 helpers) | 5 | 4 | only `TestNotificationLifecycle` runs |
| pages | 1 | 1 | 0 | |
| pivottable | 1 | 1 | 0 | smoke |
| rproxy | 1 | 1 | 0 | smoke |
| taucharts | 1 | 1 | 0 | smoke (parked) |
| tours | 1 | 1 | 0 | disabled |
| twilio | 1 | 1 | 1 | live API |
| users | 4 (+2 helpers) | 7 | 1 | |
| archetype-jar / archetype-war | 1 IT each | 1 each (generated) | 0 | |
| standalone / war | 0 | 0 | 0 | no tests |

## Appendix D — Useful commands

```bash
./mvnw versions:display-dependency-updates versions:display-plugin-updates   # what's outdated
./mvnw dependency:tree -Dincludes=<groupId>                                  # who pulls a dependency
./mvnw dependency:analyze                                                     # used-undeclared / unused-declared
./mvnw -pl orienteer-core -am verify                                          # core only
./mvnw -Pparked -pl orienteer-graph -am verify                                # try a parked module (after P1)
./mvnw org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta           # P6
```
