# orienteer-standalone

Executable uber-jar that runs Orienteer on embedded Jetty 9.4. Run it with
`java -jar orienteer-standalone.jar [--config=… --embedded --host=… --port=8080 --wait=… --help]`.

- **Entry point:** `org.orienteer.standalone.StartStandalone#main` (CLI parsing).
  `ServerRunner` builds `Server`, `ServerConnector` and a `WebAppContext` whose WAR is the jar itself.
- Bundles core, devutils, pages and pivottable. graph and bpm were dropped when they were parked (plan P1); re-add them if P8 keeps them.
- `jetty-webapp` (9.4.58; `jetty-all:uber` was dropped in P3, its JASPI module broke the start) and `javax.servlet-api` 3.1.0, compile scope.
- `src/main/resources/WEB-INF/web.xml` (Servlet 2.5, `OrienteerFilter`) and
  `src/main/resources/org/orienteer/standalone/standalone.properties`.
- Packaging: `maven-assembly-plugin` 3.8.0 with `src/assembly/uberjar.xml`: Jetty and the servlet API are unpacked into the jar
  root, everything else goes into `WEB-INF/lib`. Verified in P3: starts on JDK 21 and serves the login page.
- Run with the JVM flags from the root `AGENTS.md`: `java --add-opens java.base/java.lang=ALL-UNNAMED --enable-native-access=ALL-UNNAMED -jar ...`.
- No tests.

## Pitfalls

- The `--wait` loop in `StartStandalone` never re-reads `line`.
- `StartStandalone.main` is whitelisted in Checkstyle `UncommentedMain`. Keep the class name if you rename things.
- `Procfile` (Heroku) references this jar. Heroku support is dead (plan P10).

## Upgrade risk: HIGH

- Jetty 12 has no `jetty-all` aggregate.
- Use `jetty-server` plus `jetty-ee10-webapp` (`org.eclipse.jetty.ee10.webapp.WebAppContext`), or
  `ServletContextHandler` with a `FilterHolder(OrienteerFilter)` in a shaded jar.
- `Resource.setDefaultUseCaches` is gone.
- `web.xml` moves to the jakarta schema (plan P6).
